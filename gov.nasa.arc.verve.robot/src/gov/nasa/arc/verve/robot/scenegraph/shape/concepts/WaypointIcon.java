/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.LathedCylinder;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoFade;
import gov.nasa.arc.verve.common.ardor3d.text.BMText.AutoScale;
import gov.nasa.arc.verve.common.ardor3d.text.BMTextBackground;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;

/**
 * ported from Java3d
 * @author mallan
 *
 */
public class WaypointIcon extends Node {
    private static Logger logger = Logger.getLogger(WaypointIcon.class);
    
    protected String			m_nameString;
    protected BMText            m_nameText;
    protected BMTextBackground  m_nameTextBackground;
    protected LathedCylinder	m_centerCylinder;
    protected TexRing			m_toleranceRing;
    protected OffsetQuad        m_headingIcon;

    int m_status= Integer.MAX_VALUE;
    int m_index	= -1;

    float          m_zOffset   = -0.2f; // XXX overall offset
    final ReadOnlyVector3  m_xfmOffset;	
    final ReadOnlyVector2  m_txtOffset;

    Vector3 tv3f = new Vector3();

    public Vector3 m_pos = new Vector3( 0, 0, m_zOffset);
    final WaypointIconStatusProvider m_statusProvider;

    Vector2 m_oldRadius = new Vector2();
    Vector2 m_newRadius = new Vector2();

    WaypointDescriptor m_lastDescriptor = null;
    
    /**
     * 
     * @param item
     */
    public WaypointIcon(WaypointIconStatusProvider statusProvider, float textOffset) {
        super("wpi");
        m_statusProvider = statusProvider;

        m_txtOffset = statusProvider.getTextPivotOffset(textOffset);
        m_xfmOffset = statusProvider.getCenterXfmOffset(textOffset);

        BMText.Align align = statusProvider.getTextAlign();
        BMText.Justify justify = statusProvider.getTextJustify();
        ReadOnlyVector3 textXfmOffset = statusProvider.getTextXfmOffset(textOffset);

        m_nameString = "waypoint";
        m_nameText  = new BMText("wpi", 
                m_nameString, 
                BMFontManager.sansMedium(),
                align, justify);
        // set defaults
        m_nameText.setAutoScale(AutoScale.CapScreenSize);
        m_nameText.setAutoFade(AutoFade.FixedPixelSize);
        m_nameText.setAutoFadeFixedPixelSize(12);
        m_nameText.setAutoFadeFalloff(0.7f);
        m_nameText.setAutoRotate(true);
        m_nameText.setTranslation(textXfmOffset);
        m_nameText.setFixedOffset(m_txtOffset);
        statusProvider.setupText(m_nameText);

        MaterialState matl = new MaterialState();
        matl.setAmbient (ColorRGBA.BLACK);
        matl.setDiffuse (ColorRGBA.BLACK);
        matl.setSpecular(ColorRGBA.BLACK);
        matl.setEmissive(ColorRGBA.BLACK);
        matl.setShininess(5.0f);
        matl.setEnabled(true);
        this.setRenderState(matl);

        LightState ls = new LightState();
        ls.setTwoSidedLighting(true);
        ls.setSeparateSpecular(true);
        ls.setEnabled(true);
        this.setRenderState(ls);

        m_centerCylinder = statusProvider.createCenterCylinder(m_xfmOffset);
        m_toleranceRing  = statusProvider.createToleranceRing(m_xfmOffset);
        m_headingIcon  = statusProvider.createDirectionMarker(m_xfmOffset);

        attachChild(m_headingIcon);
        attachChild(m_nameText);
        attachChild(m_toleranceRing);
        attachChild(m_centerCylinder);
        
        if(statusProvider.showTextBackground()) {
            Texture bckTex = null;
            try{
                bckTex = RobotConcepts.getTex("black_square.png");
                bckTex.setWrap(WrapMode.Repeat);
                bckTex.setAnisotropicFilterPercent(1);
                
//                bckTex = DataBundleHelper.loadTexture("common", "images/backgrounds/black_square.png",
//                        Texture.WrapMode.Repeat, 
//                        Texture.MinificationFilter.Trilinear, 
//                        Texture.MagnificationFilter.Bilinear);
            }
            catch(Exception e) {
                logger.warn(e);
            }
            m_nameTextBackground = new BMTextBackground("TextBackground", m_nameText, bckTex);
            m_nameTextBackground.getSceneHints().setAllPickingHints(false);
            m_nameTextBackground.setBorderSize(0.2f);
            m_nameTextBackground.setTexBorderWidth(0.15f);
            m_nameTextBackground.setBackgroundColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.5f));
            m_nameTextBackground.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
            attachChild(m_nameTextBackground);
        }

        setColorFromStatus(m_status);
    }

    public double getZOffset() {
        return m_zOffset;
    }
    public WaypointIcon setZOffset(float z) {
        m_zOffset = z;
        return this;
    }
    
    public int getStatus() {
        return m_status;
    }

    public ReadOnlyVector3 getPos() {
        return m_pos;
    }

    public WaypointIconStatusProvider getStatusProvider() {
        return m_statusProvider;
    }

    protected void setDirectional(WaypointDescriptor wpd) {
        if(wpd.isDirectional) {
            m_headingIcon.getQuad().getSceneHints().setCullHint(CullHint.Inherit);
            Matrix3 rot = Matrix3.fetchTempInstance();
            rot.fromAngleNormalAxis(wpd.heading, Vector3.UNIT_Z);
            m_headingIcon.setRotation(rot);
            Matrix3.releaseTempInstance(rot);
        }
        else {
            m_headingIcon.getQuad().getSceneHints().setCullHint(CullHint.Always);
        }
    }

    protected void setVisibilityFromStatus(int status) {
        if(m_statusProvider.showToleranceFromStatus(m_status)) {
            m_toleranceRing.getSceneHints().setCullHint(CullHint.Inherit);
            m_headingIcon.getSceneHints().setCullHint(CullHint.Inherit);
        }
        else {
            m_toleranceRing.getSceneHints().setCullHint(CullHint.Always);
            m_headingIcon.getSceneHints().setCullHint(CullHint.Always);
        }

        if(m_statusProvider.showTextFromStatus(m_status)) {
            m_nameText.getSceneHints().setCullHint(CullHint.Inherit);
            if(m_nameTextBackground != null) 
                m_nameTextBackground.getSceneHints().setCullHint(CullHint.Inherit);
        }
        else {
            m_nameText.getSceneHints().setCullHint(CullHint.Always);
            if(m_nameTextBackground != null) 
                m_nameTextBackground.getSceneHints().setCullHint(CullHint.Always);
        }
    }

    public void checkTextLength() {
        //logger.debug("checkTextLength()");
    	int textLen = m_nameText.getText().length();
    	if(textLen == 0) {
    		if(m_nameText.getSceneHints().getCullHint() != CullHint.Always) {
    			m_nameText.getSceneHints().setCullHint(CullHint.Always);
    			if(m_nameTextBackground != null) {
    				m_nameTextBackground.getSceneHints().setCullHint(CullHint.Always);
    			}
    		}
    	}
    	else {
    		if(m_nameText.getSceneHints().getCullHint() != CullHint.Inherit &&
    		        m_statusProvider.showTextFromStatus(m_status)) {
    			m_nameText.getSceneHints().setCullHint(CullHint.Inherit);
    			if(m_nameTextBackground != null) {
    				m_nameTextBackground.getSceneHints().setCullHint(CullHint.Inherit);
    			}
    		}
    	}
    }
    
    /**
     * 
     * @param text text for waypoint. If null, sets defaultText.
     * @param explicit if true, set to text only; otherwise set to defaultText+"\n"+text
     */
    public void updateText(String text, boolean explicit) {
        String defaultText = m_nameString + m_statusProvider.getStringFromStatus(m_status);
        if(text == null) {
            m_nameText.setText(defaultText);
            setVisibilityFromStatus(m_status);
        }
        else {
            if(explicit) {
                m_nameText.setText(text);
            }
            else {
                m_nameText.setText(defaultText+"\n"+text);
            }
        }
        //logger.debug("updateText("+text+", "+explicit+")");
        checkTextLength();
    }

    public WaypointDescriptor getLastDescriptor() {
    	return m_lastDescriptor;
    }
    
    /**
     * 
     * @param item
     * @param z current z of rover
     * @param index
     * @param wpPath
     */
    public void updateItem(WaypointDescriptor wp, double z, 
            int index, WaypointPath wpPath, String nameOverride, boolean forceChange) {
        // check if index # changes (it shouldn't)
    	m_lastDescriptor = wp;
        m_index = index;

        String nameString;
        if(nameOverride == null || nameOverride.length() < 1) {
            nameString = wp.name;
        }
        else {
            nameString = nameOverride;
        }

        boolean itemHasChanged = forceChange;
        if( (m_status != wp.status) || !m_nameString.equals(nameString)) {
            m_status = wp.status;
            itemHasChanged = true;
        }

        WaypointPath.UpdateInfo wpUpdate = null;
        if(wpPath != null) {
            wpUpdate =  wpPath.getUpdateElement(index);
        }

        m_pos.setX(wp.location.getX());
        m_pos.setY(wp.location.getY());
        if(itemHasChanged || m_statusProvider.doTrackHeight(m_status)) {
            m_pos.setZ(z+m_zOffset);
        }

        if(wpUpdate != null) {
            wpUpdate.index = index;
            wpUpdate.pos = m_pos;
        }

        this.setTranslation(m_pos);

        if(itemHasChanged) {
            setColorFromStatus(m_status);
            setVisibilityFromStatus(m_status);
            setDirectional(wp);

            m_nameString = nameString;
            m_nameText.setText(m_nameString + m_statusProvider.getStringFromStatus(m_status));
            //logger.debug("itemHasChanged: "+m_nameString);
            checkTextLength();

            float trad = wp.locationTolerance;
            float ringThick = m_statusProvider.getToleranceThickness();
            float thick = (trad < 0.8) ? ringThick*(trad+0.2f) : ringThick;

            m_newRadius.set(trad-thick, trad+thick);
            if(!m_newRadius.equals(m_oldRadius)) {
                m_toleranceRing.setRadius(m_newRadius);
                m_oldRadius.set(m_newRadius);
            }
        }
    }

    ColorRGBA dclr = new ColorRGBA(1, 1, 0, 1);
    ColorRGBA eclr = new ColorRGBA(1, 1, 0, 1);
    ColorRGBA tmpClr = new ColorRGBA();

    void setColorFromStatus(int status) {
        m_statusProvider.getColorFromStatus(status, dclr);

        MaterialState matl;
        float amt = 0.8f;
        eclr.set(dclr.getRed()*amt, 
                dclr.getGreen()*amt, 
                dclr.getBlue()*amt, 
                1);
        matl = (MaterialState) 
        this.getLocalRenderState(RenderState.StateType.Material);
        matl.setEmissive(eclr);
        matl.setSpecular(dclr);
        this.setRenderState(matl);

        amt = 1.2f;
        tmpClr.set( 0.2f + dclr.getRed()*amt, 
                0.2f + dclr.getGreen()*amt, 
                0.2f + dclr.getBlue()*amt, 
                dclr.getAlpha());
        m_nameText.setTextColor(tmpClr);
        m_toleranceRing.setDefaultColor(tmpClr);
        m_headingIcon.setDefaultColor(tmpClr);
    }

}

package gov.nasa.arc.verve.ui.e4.widget;
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

import gov.nasa.arc.verve.ardor3d.e4.util.RotUtil;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.scenegraph.Spatial;

/**
 * Custom databound widget for editing a Spatial.
 * @author tecohen
 *
 */
public class SpatialWidget extends AbstractDatabindingWidget {
    private static final Logger logger = Logger.getLogger(SpatialWidget.class);

    public final static double D2RAD = Math.PI / 180.0;
    public final static double RAD2D = 180.0 / Math.PI;
    
    protected Text m_nameText;
    protected Text m_classText;

    protected Vector3Widget m_translationWidget;
    protected Vector3Widget m_rotationWidget;
    protected Vector3Widget m_scaleWidget;

    protected boolean m_pinned = false;

    protected Set<IChangeListener> m_changeListeners = new HashSet<IChangeListener>();

    protected static List<String> s_xyzHints = new ArrayList<String>();
    {
        s_xyzHints.add("X");
        s_xyzHints.add("Y");
        s_xyzHints.add("Z");
    }

    protected static List<String> s_rotHints = new ArrayList<String>();
    {
        s_rotHints.add("X rotation");
        s_rotHints.add("Y rotation");
        s_rotHints.add("Z rotation");
    }

    // The following change listeners apply the changes from the translation, rotation and scale to the spatial.
    protected IChangeListener m_translationChangeListener = new IChangeListener() {
        public void handleChange(ChangeEvent arg0) {
            if (getModel() != null){
                Vector3Model translationModel = (Vector3Model)m_translationWidget.getModel();
                Vector3 translation = translationModel.getVector3();
                Spatial spatial = (Spatial)getModel();
                if (!spatial.getTranslation().equals(translation)) {
                    spatial.setTranslation(translation.getX(), translation.getY(), translation.getZ());
                }
            }
        }
    };

    protected IChangeListener m_scaleChangeListener = new IChangeListener() {
        public void handleChange(ChangeEvent arg0) {
            if (getModel() != null){
                Vector3Model scalemodel = (Vector3Model)m_scaleWidget.getModel();
                Vector3 scale = scalemodel.getVector3();
                Spatial spatial = (Spatial)getModel();
                if (!spatial.getScale().equals(scale)) {
                    spatial.setScale(scale.getX(), scale.getY(), scale.getZ());
                }
            }
        }
    };

    protected IChangeListener m_rotationChangeListener = new IChangeListener() {
        public void handleChange(ChangeEvent event) {
            if (getModel() != null){
                Vector3Model rotationModel = (Vector3Model)m_rotationWidget.getModel();
                Vector3 uiEuler = rotationModel.getVector3();
                Spatial spatial = (Spatial)getModel();
                Vector3 spatialEuler = RotUtil.toEulerXYZ(spatial.getRotation(), new Vector3());
                spatialEuler.multiplyLocal(RAD2D);
                if (!spatialEuler.equals(uiEuler)){
                    Vector3 eulerRadians = uiEuler.multiply(D2RAD, new Vector3());
                    Matrix3 newMatrix = RotUtil.toMatrixXYZ(eulerRadians, new Matrix3());
                    spatial.setRotation(newMatrix);
                }
            }
        }
    };

    public SpatialWidget(Composite parent, int style) {
        super(parent, style);
        m_labelWidth = 60;
        createControls(parent);
    }

    @Override
    public void addListener(IChangeListener listener){
        super.addListener(listener);
        m_changeListeners.add(listener);
        if (m_translationWidget != null){
            m_translationWidget.addListener(listener);
            m_rotationWidget.addListener(listener);
            m_scaleWidget.addListener(listener);
        }
    }


    @Override
    public void removeListener(IChangeListener listener){
        super.removeListener(listener);
        m_changeListeners.remove(listener);
        if (m_translationWidget != null){
            m_translationWidget.removeListener(listener);
            m_rotationWidget.removeListener(listener);
            m_scaleWidget.removeListener(listener);
        }
    }

    /**
     * Add a status listener to the list to be notified
     * @param listener
     */
    @Override
    public void addListener(IStatusListener listener){
        super.addListener(listener);
        if (m_translationWidget != null){
            m_translationWidget.addListener(listener);
            m_rotationWidget.addListener(listener);
            m_scaleWidget.addListener(listener);
        }
    }

    @Override
    public void removeListener(IStatusListener listener){
        super.removeListener(listener);
        if (m_translationWidget != null){
            m_translationWidget.removeListener(listener);
            m_rotationWidget.removeListener(listener);
            m_scaleWidget.removeListener(listener);
        }
    }


    @Override
    protected IObservableValue getSelectionObservableValue(IObservableValue selection, String feature, Class returnType) {
        return PojoObservables.observeDetailValue(selection, feature, returnType);
    }

    @Override
    protected IObservableValue getObservableValue(String feature) {
        return PojoObservables.observeValue(getRealm(), getModel(), feature);
    }

    /**
     * constructs if necessary
     * @return grid data for fields
     */
    @Override
    protected GridData getLeftData() {
        if (m_leftData == null){
            m_leftData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
            m_leftData.widthHint = 240;
        }
        return m_leftData;
    }

    /**
     * Actually create the UI components
     * @param container
     */
    public void createControls(Composite container) {
        if (m_dataBindingContext == null){
            m_dataBindingContext = new DataBindingContext();
        }

        GridLayout gl = new GridLayout(2, false);
        gl.marginHeight = 0;
        gl.verticalSpacing = 0;
        setLayout(gl);

        Label nameLabel = new Label(this, SWT.TRAIL);
        nameLabel.setText("Name:");
        nameLabel.setLayoutData(getRightData());
        addChildLabel(nameLabel);

        m_nameText = new Text(this, SWT.BORDER);
        m_nameText.setLayoutData(getLeftData());
        addChildControl(m_nameText);

        Label classLabel = new Label(this, SWT.TRAIL);
        classLabel.setText("Type:");
        classLabel.setLayoutData(getRightData());
        addChildLabel(classLabel);

        m_classText = new Text(this, SWT.BORDER);
        m_classText.setLayoutData(getLeftData());
        m_classText.setEnabled(false);
        addChildControl(m_classText);
        if (getModel() != null){
            m_classText.setText(getModel().getClass().getSimpleName());
        }

        GridData multiLeft = new GridData(SWT.FILL, SWT.CENTER, true, false);
        //multiLeft.widthHint = getFieldWidth();

        Label translationLabel = new Label(this, SWT.TRAIL);
        translationLabel.setText("Translation:");
        translationLabel.setLayoutData(getRightData());
        addChildLabel(translationLabel);
        m_translationWidget = new Vector3Widget(this,SWT.NONE);
        m_translationWidget.setHints(s_xyzHints);
        m_translationWidget.setLayoutData(multiLeft);

        Label rotationLabel = new Label(this, SWT.TRAIL);
        rotationLabel.setText("Rotation:");
        rotationLabel.setLayoutData(getRightData());
        addChildLabel(rotationLabel);
        m_rotationWidget = new Vector3Widget(this,SWT.NONE);
        m_rotationWidget.setHints(s_rotHints);
        m_rotationWidget.setLayoutData(multiLeft);

        Label scaleLabel = new Label(this, SWT.TRAIL);
        scaleLabel.setText("Scale:");
        scaleLabel.setLayoutData(getRightData());
        addChildLabel(scaleLabel);
        m_scaleWidget = new Vector3Widget(this,SWT.NONE);
        m_scaleWidget.setHints(s_xyzHints);
        m_scaleWidget.setLayoutData(multiLeft);
    }

    @Override
    public void setModel(Object obj) {
        if (obj == null || m_pinned){
            return;
        }

        // There's a dastardly bug in the databinding where when the name is null, it jacks up the binding of the name field after selection changes.
        unbindUI();
        m_model = obj;
        populateModel((Spatial)obj);

        bindUI(getRealm());
    }

    /**
     * only called by set model
     * @param spatial
     */
    protected void populateModel(Spatial spatial){
        if (m_pinned){
            return;
        }
        if (spatial == null){
            return;
        }
        m_model = spatial;
        if (m_classText != null){
            String className = "UNDEFINED";
            if (spatial.getClass().getSimpleName() != null) {
                className = spatial.getClass().getSimpleName();
            }
            m_classText.setText(className);
        }

        if(false) {
            logger.debug(spatial.getName()+"         CullHint="+spatial.getSceneHints().getLocalCullHint());
            logger.debug(spatial.getName()+"         DataMode="+spatial.getSceneHints().getLocalDataMode());
            logger.debug(spatial.getName()+" RenderBucketType="+spatial.getSceneHints().getLocalRenderBucketType());
            logger.debug(spatial.getName()+" TransparencyType="+spatial.getSceneHints().getLocalTransparencyType());
        }

        populateExtras(spatial);
    }

    /**
     * Populate the models for translation, rotation and scale
     * We add the change listeners every time because we are unbinding and rebinding these widgets and the bindings change each time; the listeners are on the bindings.
     * We don't have to explicitly remove the listeners because the act of setting the model unbinds, which destroys the bindings which have the listeners.
     * @param spatial
     */
    protected void populateExtras(Spatial spatial){
        ReadOnlyTransform transform = spatial.getTransform();
        if (transform != null && this.m_translationWidget != null){

            Vector3 translation = new Vector3(spatial.getTranslation());
            m_translationWidget.setModel(new Vector3Model(translation));
            m_translationWidget.addListener(m_translationChangeListener);

            Vector3 eulerDegrees = RotUtil.toEulerXYZ(spatial.getRotation(), new Vector3());
            eulerDegrees.multiplyLocal(RAD2D);
            m_rotationWidget.setModel(new Vector3Model(eulerDegrees));
            m_rotationWidget.addListener(m_rotationChangeListener);

            Vector3 scale = new Vector3(spatial.getScale());
            m_scaleWidget.setModel(new Vector3Model(scale));
            m_scaleWidget.addListener(m_scaleChangeListener);

            for (IChangeListener l : m_changeListeners){
                m_translationWidget.addListener(l);
                m_rotationWidget.addListener(l);
                m_scaleWidget.addListener(l);
            }
        }
    }


    @Override
    public boolean bindUI(Realm realm) {
        if (!isBound()){
            setBound(bind("name", m_nameText));
            return isBound();
        }
        return false;
    }

    public boolean isPinned() {
        return m_pinned;
    }

    public void setPinned(boolean pinned) {
        m_pinned = pinned;
    }

}

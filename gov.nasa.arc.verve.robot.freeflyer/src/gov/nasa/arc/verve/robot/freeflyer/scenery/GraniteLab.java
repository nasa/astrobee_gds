package gov.nasa.arc.verve.robot.freeflyer.scenery;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Box;

public class GraniteLab extends Node {
	// all measurements in m
	protected double m_tableHeight = 0.51;
	protected double m_tableWidth = 3.05;
	protected double m_frameWidth = 2.07;
	protected double m_frameHeight = 1.83;
	protected double m_frameDepth = 2.11;
	protected double m_8020width = 0.025;
	protected double m_sceneOffsetZ = 0;
	
	protected double fudgeDockX = 0.02;
	protected double fudgeDockZ = 0.54;
	
	protected Node m_frameBase;

	public GraniteLab() {
		super("GraniteLab");
		init();
	}
	
	public void init() {
		makeTable();
		makeFrame();
		makeDock();
	}

	private void makeFrame() {
		m_frameBase = new Node("Frame");
		attachChild(m_frameBase);
		
		// make lower 8020
		make8020("lowerNorth", new Vector3(0, -m_frameWidth/2.0, 0), m_frameDepth/2.0, m_8020width/2.0, m_8020width/2.0);	
		make8020("lowerSouth", new Vector3(0, m_frameWidth/2.0, 0), m_frameDepth/2.0, m_8020width/2.0, m_8020width/2.0);	
		make8020("lowerEast", new Vector3(-m_frameWidth/2.0, 0, 0), m_8020width/2.0, m_frameWidth/2.0, m_8020width/2.0);
		make8020("lowerWest", new Vector3(m_frameWidth/2.0, 0, 0), m_8020width/2.0, m_frameWidth/2.0, m_8020width/2.0);

		// make upper 8020
		make8020("upperNorth", new Vector3(0, -m_frameWidth/2.0, m_frameHeight), m_frameDepth/2.0, m_8020width/2.0, m_8020width/2.0);	
		make8020("upperSouth", new Vector3(0, m_frameWidth/2.0, m_frameHeight), m_frameDepth/2.0, m_8020width/2.0, m_8020width/2.0);	
		make8020("upperEast", new Vector3(-m_frameWidth/2.0, 0, m_frameHeight), m_8020width/2.0, m_frameWidth/2.0, m_8020width/2.0);
		make8020("upperWest", new Vector3(m_frameWidth/2.0, 0, m_frameHeight), m_8020width/2.0, m_frameWidth/2.0, m_8020width/2.0);

		// make sides
		make8020("southwest", new Vector3(m_frameDepth/2.0, m_frameWidth/2.0, m_frameHeight/2.0), m_8020width/2.0, m_8020width/2.0, m_frameHeight/2.0);	
		make8020("southeast", new Vector3(-m_frameDepth/2.0, m_frameWidth/2.0, m_frameHeight/2.0), m_8020width/2.0, m_8020width/2.0, m_frameHeight/2.0);	
		make8020("northwest", new Vector3(m_frameDepth/2.0, -m_frameWidth/2.0, m_frameHeight/2.0), m_8020width/2.0, m_8020width/2.0, m_frameHeight/2.0);	
		make8020("northeast", new Vector3(-m_frameDepth/2.0, -m_frameWidth/2.0, m_frameHeight/2.0), m_8020width/2.0, m_8020width/2.0, m_frameHeight/2.0);	
	}

	private void make8020(String name, Vector3 offset, double xSize, double ySize, double zSize) {
		MaterialState mtl = makeMaterialState(  0.9f, 0.9f, 0.9f );
		offset.setZ(-offset.getZ());
		offset.addLocal(0, 0, -m_sceneOffsetZ);
		
		Box bar = new Box(name, offset, xSize, ySize, zSize);	
		bar.setRenderState(mtl);
		bar.getSceneHints().setCullHint(CullHint.Never);
		m_frameBase.attachChild(bar);
	}

	private void makeDock() {
		double halfXSide = 0.04;
		double halfYSide = 0.39;
		double halfZSide = 0.23;
		
		double distFromPlusYSide = 0.34;
		double distFromTableSurface = 0.46;
		
		double xOffset = -m_frameWidth/2 + halfXSide + 0.10 + fudgeDockX;
		double yOffset = m_frameDepth/2 - halfYSide - distFromPlusYSide;
		double zOffset = m_tableHeight/2.0 - halfZSide - distFromTableSurface + 0.02 - fudgeDockZ;
		
		Vector3 dockCenter = new Vector3();//xOffset, yOffset, zOffset);
		//Transform xfm = new Transform();

		Box dock = new Box("dock", dockCenter, halfXSide, halfYSide, halfZSide);
		
		MaterialState mtl = makeMaterialState(0.8f, 0.8f, 0.8f);
		dock.setRenderState(mtl);
		dock.getSceneHints().setCullHint(CullHint.Never);
		Matrix3 rotation =  new Matrix3();
		rotation.fromAngleNormalAxis(30*Math.PI/180, Vector3.UNIT_Y);
		dock.setRotation(rotation);
		
		dock.setTranslation(xOffset, yOffset, zOffset);
		
		double portXSize = 0.08;
		double portYSize = 0.0575;
		double portZSize = 0.05;
		
		double distPortSticksOutIntoCenterOfTable = 0.34;
		
		double portX = -m_frameWidth/2 - portXSize + distPortSticksOutIntoCenterOfTable + fudgeDockX;
		
		double portOffsetFromDockEdgeOnPlusY = 0.08;
		double portDistFromPlusYSide = distFromPlusYSide + portOffsetFromDockEdgeOnPlusY;
		double portYleft = m_frameDepth/2 - portYSize - portDistFromPlusYSide;
		
		double distBtwPorts = 0.38;
		double portYright = portYleft - distBtwPorts - portYSize*2;
		
		double heightOfBottomOfPort = 0.545;
		double portZ = m_tableHeight/2.0 - portZSize - heightOfBottomOfPort - fudgeDockZ;
		
		Vector3 leftPortCenter = new Vector3(portX, portYleft, portZ);
		Vector3 rightPortCenter = new Vector3(portX, portYright, portZ);
		
		Box leftPort = new Box("leftPort", leftPortCenter, portXSize, portYSize, portZSize);
		Box rightPort = new Box("rightPort", rightPortCenter, portXSize, portYSize, portZSize);
		
		attachChild(leftPort);
		attachChild(rightPort);
		
		attachChild(dock);
	}
	
	private void makeTable() {
		MaterialState mtl = makeMaterialState( 0.4f, 0.4f, 0.4f );

		Vector3 tableCenter = new Vector3();
		Box graniteTable = new Box("graniteTable", tableCenter, m_tableWidth/2.0, m_tableWidth/2.0, m_tableHeight/2.0);
		Transform xfm = new Transform();

		xfm.setTranslation(0, 0, +m_tableHeight/2.0 - m_sceneOffsetZ);
	
		graniteTable.setRenderState(mtl);
		graniteTable.setTransform(xfm);
		graniteTable.getSceneHints().setCullHint(CullHint.Never);
		attachChild(graniteTable);
	}
	
	private MaterialState makeMaterialState(float r, float g, float b) {
		float diff = 0.5f;
		float spec = 0.0f;
		float emis = 0.5f;
		float ambt = (spec+diff)/4;
		float[] color = new float[] {r, g, b};
		
		MaterialState mtl = new MaterialState();
		mtl.setShininess(5);
		mtl.setDiffuse (new ColorRGBA(diff*color[0], diff*color[1], diff*color[2],1));
		mtl.setSpecular(new ColorRGBA(spec*color[0], spec*color[1], spec*color[2],1));
		mtl.setEmissive(new ColorRGBA(emis*color[0], emis*color[1], emis*color[2],1));
		mtl.setAmbient (new ColorRGBA(ambt*color[0], ambt*color[1], ambt*color[2],1));
		return mtl;
	}
}

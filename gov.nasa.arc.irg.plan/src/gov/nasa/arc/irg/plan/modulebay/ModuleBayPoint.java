/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.modulebay;

import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.CoordinatesGenerator;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.model.modulebay.Point3D;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonSetter;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.eclipse.core.runtime.Platform;

@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")
@JsonSerialize(using = ModuleBayPointSerializer.class)
@JsonDeserialize(using = ModuleBayPointDeserializer.class)
public class ModuleBayPoint extends Point6Dof {
	// angles in DEGREES because they are bound to the GUI widgets
	private boolean moduleBayValid = false;
	private ModuleName module;
	private boolean bayNumberValid = false;
	private BayNumber bayNumber;
	private boolean centerOne = true;
	private LocationMap.Wall wallOne = LocationMap.Wall.DECK;
	private double wallOffsetOne = 0.2;
	private boolean centerTwo = true;
	private LocationMap.Wall wallTwo = LocationMap.Wall.DECK;
	private double wallOffsetTwo = 0.2;
	private boolean ignoreOrientation = true;
	private LocationMap.Wall orientationWall = LocationMap.Wall.FWD;
	private final double epsilon = 0.00001;
	private StationBookmark bookmark = null;
	private boolean bookmarkValid = false;
	public final double RAD2DEG = 180.0/Math.PI;
	protected DecimalFormat df = new DecimalFormat();
	protected int precision = 2; // allow only 2 decimal places
	
	// Can't include this in WorkbenchConstants.java because of dependencies
	public final String PRECISION_STRING = "-precision";
	
	protected int changePrecisionFromCommandLine() {
		String[] args = Platform.getCommandLineArgs();

		for(int i=0; i<args.length-1; i++) {
			if(args[i].equals(PRECISION_STRING)) {
				return Integer.parseInt(args[i+1]);
			}
		}
		return precision;
	}
	
	public ModuleBayPoint() {
		super();
		precision = changePrecisionFromCommandLine();
		
		df.setMinimumFractionDigits(precision);
	    df.setMaximumFractionDigits(precision);
	    df.setRoundingMode(RoundingMode.HALF_UP);
	}

	public ModuleBayPoint(StationBookmark inBookmark) {
		setBookmark(inBookmark);
	}

	public ModuleBayPoint(Point6Dof other) {
		if(other instanceof ModuleBayPoint) {
			initializeFromModuleBayPoint((ModuleBayPoint)other);
		}
		else {
			initializeFromPoint6Dof(other);
		}
	}

	public ModuleBayPoint(ModuleBayPoint other) {
		initializeFromModuleBayPoint(other);
	}

	protected void initializeFromModuleBayPoint(ModuleBayPoint other) {
		if(other.isBookmarkValid()) {
			setBookmark(other.getBookmark());
		}
		else if(other.isModuleBayValid()) {
			setModule(other.getModule());
			if(other.getBayNumber() != null) {
				setBayNumber(other.getBayNumber());
			}
			setCenterOne(other.isCenterOne());
			if(!isCenterOne()) {
				setWallOne(other.getWallOne());
				setWallOneOffset(other.getWallOneOffset());
			}
			setCenterTwo(other.isCenterTwo());
			if(!isCenterTwo()) {
				setWallTwo(other.getWallTwo());
				setWallTwoOffset(other.getWallTwoOffset());
			}
			setIgnoreOrientation(other.isIgnoreOrientation());
			if(!isIgnoreOrientation()) {
				setOrientationWall(other.getOrientationWall());
			}
		} else {
			initializeFromPoint6Dof(other);
			setIgnoreOrientation(other.isIgnoreOrientation());
		}
	}

	protected void initializeFromPoint6Dof(Point6Dof other) {
		setX(other.getX());
		setY(other.getY());
		setZ(other.getZ());
		setRoll(other.getRoll());
		setPitch(other.getPitch());
		setYaw(other.getYaw());
		setModuleBayValid(false);
		invalidateBookmark();
	}

	/** Angles in degrees */
	public ModuleBayPoint(float x, float y, float z, float roll, float pitch, float yaw) {
		this.x = round(x);
		this.y = round(y);
		this.z = round(z);
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
		ignoreOrientation = false;
		setModuleBayValid(false);
		invalidateBookmark();
	}

	public ModuleBayPoint(float x, float y, float z) {
		this(x, y, z, 0, 0, 0);
		setIgnoreOrientation(true);
	}

	// XXX This worries me ... comes from Plan.initialize() but shouldn't
	// we have more data than is in Position??? - DW 3/24/16
	public ModuleBayPoint(Position pos) {
		x = round(pos.getCoordinates().get(0));
		y = round(pos.getCoordinates().get(1));
		z = round(pos.getCoordinates().get(2));
		if(pos.hasOrientation()) {
			roll = pos.getOrientation().get(0);
			pitch = pos.getOrientation().get(1);
			yaw = pos.getOrientation().get(2);
		} else {
			setAnglesToZero();
		}
		setModuleBayValid(false);
		invalidateBookmark();
	}

	public void setBookmark(StationBookmark inBookmark) {
		StationBookmark old = this.bookmark;
		this.bookmark = inBookmark;
		firePropertyChange("bookmark", old, this.bookmark);
		setBookmarkValid(true);

		recalculateLocation();
	}

	// Not called
	public void setBookmarkFromString(String bookmarkName) {
		StationBookmark sb = BookmarkListBuilder.getBookmarkFromName(bookmarkName);
		setBookmark(sb);
	}

	// databinding needs all inputs to be checked so the object stays consistent
	@JsonIgnore
	public void setModule(ModuleName module) {
		if(module == null || module.equals(this.module)) {
			return;
		}
		uncheckedSetModule(module);
		setIgnoreOrientation(true);
		setModuleBayValid(true);
		bayNumberValid = false;
		invalidateBookmark();
		recalculateLocation();
	}

	@JsonIgnore
	public void setBayNumber(BayNumber bayNumber) {
		if(!LocationMap.getInstance().validateBayNumber(module, bayNumber)) {
			throw new IllegalArgumentException("Invalid bay number.");
		}
		uncheckedSetBayNumber(bayNumber);
		setModuleBayValid(true);
		bayNumberValid = true;
		invalidateBookmark();
		recalculateLocation();
	}

	@JsonIgnore
	public void setCenterOne(boolean centerOne) {
		uncheckedSetCenterOne(centerOne);
		invalidateBookmark();
		if(centerOne) {
			centerTwo = true;
		}
		else {
			if(!LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallOne, wallOffsetOne)) {
				// what if centerOne is set to false, but Wall One is not defined yet?
				// Don't throw error, but don't recalculate
				return;
			}
		}
		recalculateLocation();
	}

	@JsonIgnore
	public void setWallOne(LocationMap.Wall wallOne) {
		if(!LocationMap.getInstance().validateModuleWall(module, wallOne)) {
			throw new IllegalArgumentException(wallOne+" is not valid wall in "+module);
		}
		invalidateBookmark();
		uncheckedSetWallOne(wallOne);
		recalculateLocation();
	}

	@JsonIgnore
	public void setWallOneOffset(double wallOffsetOne) {
		uncheckedSetWallOffsetOne(wallOffsetOne);
		invalidateBookmark();
		if(centerOne) {
			return;
		}

		if(!LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallOne, wallOffsetOne)) {
			throw new IllegalArgumentException(module + " bay "+ bayNumber + ", " + wallOffsetOne + "m from "+wallOne+" is invalid");
		}

		recalculateLocation();
	}

	@JsonIgnore
	public void setCenterTwo(boolean centerTwo) {
		if(centerOne && !centerTwo) {
			throw new IllegalArgumentException("Cannot center Wall 2 without centering Wall 1.");
		}
		uncheckedSetCenterTwo(centerTwo);
		invalidateBookmark();

		if(!LocationMap.getInstance().validateWallSelections(wallOne, wallTwo)
				|| !LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallTwo, wallOffsetTwo)) {
			// what if centerTwo is set to false, but Wall Two is not defined yet?
			// Don't throw error, but don't recalculate
			return;
		}
		recalculateLocation();
	}

	@JsonIgnore
	public void setWallTwo(LocationMap.Wall wallTwo) {
		if(!LocationMap.getInstance().validateModuleWall(module, wallTwo)) {
			throw new IllegalArgumentException(wallTwo+" is not valid wall in "+module);
		}
		uncheckedSetWallTwo(wallTwo);
		invalidateBookmark();
		recalculateLocation();
	}

	@JsonIgnore
	public void setWallTwoOffset(double wallOffsetTwo) {
		uncheckedSetWallOffsetTwo(wallOffsetTwo);
		invalidateBookmark();
		if(centerTwo) {
			return;
		}

		if(!LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallTwo, wallOffsetTwo)) {
			throw new IllegalArgumentException(module + " bay "+ bayNumber + ", " + wallOffsetTwo + "m from "+wallTwo+" is invalid");
		}

		recalculateLocation();
	}

	public void setIgnoreOrientation(boolean ignoreOrientation) {
		uncheckedSetIgnoreOrientation(ignoreOrientation);
		// TODO check logic of next line
		invalidateBookmark();
	}

	private void invalidateBookmark() {
		bookmark = null;
		setBookmarkValid(false);
	}

	@JsonSetter("moduleBayValid")
	public void setModuleBayValidJsonOnly(boolean valid) {
		// this should only be called by json serializer
		moduleBayValid = valid;
	}

	@JsonSetter("bayNumberValid")
	public void setBayNumberValidJsonOnly(boolean valid) {
		// this should only be called by json serializer
		bayNumberValid = valid;
	}

	private void recalculateLocation() {
		if(bookmarkValid) {
			recalculateFromBookmark();
			return;
		}
		if(!moduleBayValid) {
			return;
		}
		recalculateFromModuleBay();
	}

	private void recalculateFromModuleBay() {
		if(!bayNumberValid) {
			Point3D ans = CoordinatesGenerator.getCoordinates(module);
			setCoordinatesTo(ans);
			return;
		}
		if(centerOne && centerTwo) {
			Point3D ans = CoordinatesGenerator.getCoordinates(module, bayNumber);
			setCoordinatesTo(ans);
			return;
		}
		// need to validate that chosen wall is correct and distance is okay
		if(!LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallOne, wallOffsetOne)) {
			System.out.println("Wait a second");
			LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallOne, wallOffsetOne);
			throw new IllegalStateException(module + ", Bay "+bayNumber+", "+wallOffsetOne+"m from "+wallOne+" is inconsistent");
		}

		if(!centerOne && centerTwo) {
			Point3D ans = CoordinatesGenerator.getCoordinates(module, bayNumber, 
					wallOne, wallOffsetOne);
			setCoordinatesTo(ans);
			return;
		}

		if(!LocationMap.getInstance().validateModuleWallAndDistance(module, bayNumber, wallTwo, wallOffsetTwo)) {
			throw new IllegalStateException(module + ", Bay "+bayNumber+", "+wallOffsetTwo+"m from "+wallTwo+" is inconsistent");
		}

		if(!centerOne && !centerTwo) {
			Point3D ans = CoordinatesGenerator.getCoordinates(module, bayNumber, 
					wallOne, wallOffsetOne, wallTwo, wallOffsetTwo);
			setCoordinatesTo(ans);
			return;
		}
	}

	private void recalculateFromBookmark() {
		StationBookmark save = bookmark;
		if(((ModuleBayPoint)bookmark.getLocation()).isModuleBayValid()) {
			ModuleBayPoint other = (ModuleBayPoint) bookmark.getLocation();
			if(other.isModuleBayValid()) {
				setModule(other.getModule());
				if(other.isBayNumberValid()) {
					setBayNumber(other.getBayNumber());
				}
				if(!other.isCenterOne()) {
					setCenterOne(false);
					setWallOneOffset(other.getWallOneOffset());
					setWallOne(other.getWallOne());
				}
				if(!other.isCenterTwo()) {
					setCenterTwo(false);
					setWallTwoOffset(other.getWallTwoOffset());
					setWallTwo(other.getWallTwo());
				}
				//				setCustomOrientation(other.isCustomOrientation());
				setIgnoreOrientation(other.isIgnoreOrientation());
				if(!ignoreOrientation) {
					setOrientationWall(other.getOrientationWall());
				}
			} 
		} else {
			if(bookmark.getLocation() instanceof ModuleBayPoint) {
				ModuleBayPoint other = (ModuleBayPoint) bookmark.getLocation();
				setX(other.getX());
				setY(other.getY());
				setZ(other.getZ());
				setRoll(other.getRoll());
				setPitch(other.getPitch());
				setYaw(other.getYaw());
				setIgnoreOrientation(other.isIgnoreOrientation());
				setModuleBayValid(false);
			} else {
			Point6Dof other = bookmark.getLocation();
			setX(other.getX());
			setY(other.getY());
			setZ(other.getZ());
			setRoll(other.getRoll());
			setPitch(other.getPitch());
			setYaw(other.getYaw());
			// If we don't have a ModuleBayPoint, we don't have anywhere for info about
			// whether to ignore the orientation.  If we need this, need to rewrite Point6Dof
			// or insert another class in the hierarchy that is Point6Dof + ignoreOrientation
			setIgnoreOrientation(false);
			setModuleBayValid(false);
			}
		}
		bookmark = save;
		setBookmarkValid(true);
	}

	private void setCoordinatesTo(Point3D other) {
		uncheckedSetX((float) other.getX());
		uncheckedSetY((float) other.getY());
		uncheckedSetZ((float) other.getZ());
	}

	public void uncheckedSetX(float x) {
		float oldx = this.x;
		this.x = round(x);
		firePropertyChange("x", oldx, x);
	}

	public void uncheckedSetY(float y) {
		float oldy = this.y;
		this.y = round(y);
		firePropertyChange("y", oldy, y);
	}

	public void uncheckedSetZ(float z) {
		float oldz = this.z;
		this.z = round(z);
		firePropertyChange("z", oldz, z);
	}

	@Override
	public void setX(float x) {
		uncheckedSetX(x);
		// if set from outside, module bay is inconsistent
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	@Override
	public void setY(float y) {
		uncheckedSetY(y);
		// if set from outside, module bay is inconsistent
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	@Override
	public void setZ(float z) {
		uncheckedSetZ(z);
		// if set from outside, module bay is inconsistent
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	/** 
	 * in RADIANS
	 * meant to be used only by PlanCompiler, updating rotation to be
	 * from direction of motion when ignoreOrientation is true
	 */
	public void uncheckedSetRollRadians(double rollRadians) {
		if(ignoreOrientation) {
			super.setRoll(Math.round(rollRadians * RAD2DEG));
		}
	}
	
	/** 
	 * in RADIANS
	 * meant to be used only by PlanCompiler, updating rotation to be
	 * from direction of motion when ignoreOrientation is true
	 */
	public void uncheckedSetPitchRadians(double pitchRadians) {
		if(ignoreOrientation) {
			super.setPitch(Math.round(pitchRadians * RAD2DEG));
		}
	}
	
	/** 
	 * in RADIANS
	 * meant to be used only by PlanCompiler, updating rotation to be
	 * from direction of motion when ignoreOrientation is true
	 */
	public void uncheckedSetYawRadians(double yawRadians) {
		if(ignoreOrientation) {
			super.setYaw(Math.round(yawRadians * RAD2DEG));
		}
	}
	
	@Override
	/** in degrees */
	public void setRoll(float roll) {
		ignoreOrientation = false;
		super.setRoll(roll);
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	@Override
	/** in degrees */
	public void setPitch(float pitch) {
		ignoreOrientation = false;
		super.setPitch(pitch);
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	@Override
	/** in degrees */
	public void setYaw(float yaw) {
		ignoreOrientation = false;
		super.setYaw(yaw);
		setModuleBayValid(false);
		setBookmarkValid(false);
	}

	public void setOrientationWall(LocationMap.Wall wall) {
		this.orientationWall = wall;
		setRoll(0);
		switch(wall) {
		case FWD:
			setPitch(0);
			setYaw(0);
			break;
		case AFT:
			setPitch(0);
			setYaw(180);
			break;
		case STBD:
			setPitch(0);
			setYaw(90);
			break;
		case PORT:
			setPitch(0);
			setYaw(-90);
			break;
		case DECK:
			setPitch(-90);
			setYaw(0);
			break;
		case OVHD:
			setPitch(90);
			setYaw(0);
			break;
		}
		setBookmarkValid(false);
	}

	@JsonSetter("module")
	public void uncheckedSetModule(ModuleName module) {
		ModuleName oldModule = this.module;
		this.module = module;
		firePropertyChange("module", oldModule, this.module);
	}

	@JsonSetter("bayNumber")
	public void uncheckedSetBayNumber(BayNumber bayNumber) {
		BayNumber oldBayNumber = this.bayNumber;
		this.bayNumber = bayNumber;
		firePropertyChange("bayNumber", oldBayNumber, this.bayNumber);
	}

	@JsonSetter("centerOne")
	public void uncheckedSetCenterOne(boolean centerOne) {
		boolean oldCenterOne = this.centerOne;
		this.centerOne = centerOne;
		firePropertyChange("centerOne", oldCenterOne, this.centerOne);
	}

	@JsonSetter("wallOne")
	public void uncheckedSetWallOne(LocationMap.Wall wallOne) {
		LocationMap.Wall oldWallOne = this.wallOne;
		this.wallOne = wallOne;
		firePropertyChange("wallOne", oldWallOne, this.wallOne);
	}

	@JsonSetter("wallOffsetOne")
	public void uncheckedSetWallOffsetOne(double wallOffsetOne) {
		double oldWallOneOffset = this.wallOffsetOne;
		this.wallOffsetOne = wallOffsetOne;
		firePropertyChange("wallOffsetOne", oldWallOneOffset, this.wallOffsetOne);
	}

	@JsonSetter("centerTwo")
	public void uncheckedSetCenterTwo(boolean centerTwo) {
		boolean oldCenterTwo = this.centerTwo;
		this.centerTwo = centerTwo;
		firePropertyChange("centerTwo", oldCenterTwo, this.centerTwo);
	}

	@JsonSetter("wallTwo")
	public void uncheckedSetWallTwo(LocationMap.Wall wallTwo) {
		LocationMap.Wall oldWallTwo = this.wallTwo;
		this.wallTwo = wallTwo;
		firePropertyChange("wallTwo", oldWallTwo, this.wallTwo);
	}

	@JsonSetter("wallOffsetTwo")
	public void uncheckedSetWallOffsetTwo(double wallOffsetTwo) {
		double oldWallTwoOffset = this.wallOffsetTwo;
		this.wallOffsetTwo = wallOffsetTwo;
		firePropertyChange("wallOffsetTwo", oldWallTwoOffset, this.wallOffsetTwo);
	}

	@JsonSetter("ignoreOrientation")
	public void uncheckedSetIgnoreOrientation(boolean ignoreOrientation) {
		boolean oldIgnoreOrientation = this.ignoreOrientation;
		this.ignoreOrientation = ignoreOrientation;
		firePropertyChange("ignoreOrientation", oldIgnoreOrientation, this.ignoreOrientation);
	}

	@JsonSetter("orientationWall")
	public void uncheckedSetOrientationWall(LocationMap.Wall orientationWall) {
		LocationMap.Wall oldOrientationWall = this.orientationWall;
		this.orientationWall = orientationWall;
		firePropertyChange("orientationWall", oldOrientationWall, this.orientationWall);
	}

	private void setModuleBayValid(boolean valid) {
		boolean oldValue = moduleBayValid;
		this.moduleBayValid = valid;
		firePropertyChange("moduleBayValid",oldValue, moduleBayValid);
	}

	private void setBookmarkValid(boolean valid) {
		boolean oldValue = bookmarkValid;
		this.bookmarkValid = valid;
		firePropertyChange("bookmarkValid",oldValue, bookmarkValid);
	}

	/** degrees */
	public void setRpyDontFirePropertyChange(float roll, float pitch, float yaw) {
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public boolean isModuleBayValid() {
		return moduleBayValid;
	}

	public ModuleName getModule() {
		return module;
	}
	// see if this works with Eclipse databinding
	@JsonIgnore
	public ModuleName getModuleDatabinding() {
		return module;
	}
	public boolean isBayNumberValid() {
		return bayNumberValid;
	}
	@JsonIgnore
	public boolean isBayNumberValidDatabinding() {
		return bayNumberValid;
	}
	public BayNumber getBayNumber() {
		return bayNumber;
	}
	@JsonIgnore
	public BayNumber getBayNumberDatabinding() {
		return bayNumber;
	}

	public boolean isCenterOne() {
		return centerOne;
	}

	public double getWallOneOffset() {
		return wallOffsetOne;
	}
	public LocationMap.Wall getWallOne() {
		return wallOne;
	}
	public boolean isCenterTwo() {
		return centerTwo;
	}
	public LocationMap.Wall getWallTwo() {
		return wallTwo;
	}
	public double getWallTwoOffset() {
		return wallOffsetTwo;
	}
	public boolean isIgnoreOrientation() {
		return ignoreOrientation;
	}
	public LocationMap.Wall getOrientationWall() {
		return orientationWall;
	}
	public StationBookmark getBookmark() {
		return bookmark;
	}
	public boolean isBookmarkValid() {
		return bookmarkValid;
	}

	protected float round(float d) {
		String numString = df.format(d);
		return Float.valueOf(numString);
	}
	
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer(getClass().getSimpleName() + " ");

		if(bookmarkValid) {
			result.append(bookmark.toString() + ": ");
		}

		if(moduleBayValid) {
			result = new StringBuffer(module.toString() + " " + bayNumber);
			if(centerOne && centerTwo) {
				result.append(", centered");
			}

			if(!centerOne) {
				result.append(", " + wallOffsetOne + " m from " + wallOne.toString());
			}
			if(!centerTwo) {
				result.append(", " + wallOffsetTwo + " m from " + wallTwo.toString());
			}

			if(!ignoreOrientation) {
				result.append(", facing " + orientationWall);
			} else {
				result.append(", no orientationWall");
			}
		}
		else {
			result.append(" ");
			result.append(getCoordinatesString());
			if(ignoreOrientation) {
				result.append(", ignore");
			}
		}
		return result.toString();
	}

	@JsonIgnore
	public String getCoordinatesString() {
		StringBuffer result = new StringBuffer();
		result.append(x + ", " + y + ", " + z);
		result.append("; ");
		result.append(roll + ", " + pitch + ", " + yaw);
		return result.toString();
	}

	@Override
	public String toShortString() {
		StringBuffer result = new StringBuffer();

		if(bookmarkValid) {
			result.append(bookmark.toString() + ": ");
		}

		if(moduleBayValid) {
			result.append(module.toString() + " " + bayNumber);

			if(!centerOne) {
				result.append(", " + wallOffsetOne + "m from " + wallOne.toString());
			}
			if(!centerTwo) {
				result.append(", " + wallOffsetTwo + "m from " + wallTwo.toString());
			}

			if(!ignoreOrientation) {
				result.append(", facing " + orientationWall);
			} 

		} else {
			result.append(getCoordinatesString());
		}
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (moduleBayValid ? 1 :0);
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((bayNumber == null) ? 0 : bayNumber.hashCode());
		result = prime * result + (centerOne ? 1 :0);
		result = prime * result + ((wallOne == null) ? 0 : wallOne.hashCode());
		result = prime * result + (int)Double.doubleToLongBits(wallOffsetOne);
		result = prime * result + (centerTwo ? 1 :0);
		result = prime * result + ((wallTwo == null) ? 0 : wallTwo.hashCode());
		result = prime * result + (int)Double.doubleToLongBits(wallOffsetTwo);
		result = prime * result + (ignoreOrientation ? 1 :0);
		result = prime * result + ((orientationWall == null) ? 0 : orientationWall.hashCode());
		result = prime * result + ((bookmark == null) ? 0 : bookmark.hashCode());
		result = prime * result + (bookmarkValid ? 1 :0);
		return result;
	}

	@Override
	public ModuleBayPoint clone() throws CloneNotSupportedException {
		ModuleBayPoint ret = (ModuleBayPoint)super.clone();
		if(bookmark != null) {
			ret.setBookmark(bookmark);
		}
		else if(moduleBayValid) {
			ret.setModule(module);
			if(bayNumberValid) {
				ret.setBayNumber(bayNumber);
			}
			if(!centerOne) {
				ret.setCenterOne(centerOne);
				ret.setWallOneOffset(wallOffsetOne);
				ret.setWallOne(wallOne);
			}
			if(!centerTwo) {
				ret.setCenterTwo(centerTwo);
				ret.setWallTwoOffset(wallOffsetTwo);
				ret.setWallTwo(wallTwo);
			}
			ret.setIgnoreOrientation(ignoreOrientation);
			if(!ignoreOrientation) {
				ret.setOrientationWall(orientationWall);
			}
		} else {
			ret.setX(x);
			ret.setY(y);
			ret.setZ(z);
			ret.setRoll(roll);
			ret.setPitch(pitch);
			ret.setYaw(yaw);
			ret.setIgnoreOrientation(ignoreOrientation);
		}
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if( !(obj instanceof ModuleBayPoint)) {
			return false;
		}
		ModuleBayPoint other = (ModuleBayPoint) obj;

		if(!other.isModuleBayValid()) {
			return super.equals(new Point6Dof(x, y, z, roll, pitch, yaw));
		}

		if(module == null) {
			if(other.getModule() != null) {
				return false;
			}
		} else if(!module.equals(other.getModule())) {
			return false;
		}

		else if(bayNumber != other.getBayNumber()) {
			return false;
		}

		if(centerOne != other.isCenterOne()) {
			return false;
		}

		if(wallOne == null) {
			if(other.getWallOne() != null) {
				return false;
			}
		} else if(!wallOne.equals(other.getWallOne())) {
			return false;
		}

		if(!(Math.abs(wallOffsetOne-other.getWallOneOffset()) < epsilon)) {
			return false;
		}
		if(centerTwo != other.isCenterTwo()) {
			return false;
		}

		if(wallTwo == null) {
			if(other.getWallTwo() != null) {
				return false;
			}
		} else if(!wallTwo.equals(other.getWallTwo())) {
			return false;
		}

		if(!(Math.abs(wallOffsetTwo-other.getWallTwoOffset()) < epsilon)) {
			return false;
		}
		if(ignoreOrientation != other.isIgnoreOrientation()) {
			return false;
		}
		if(!ignoreOrientation) {
			if(orientationWall == null) {
				if(other.getOrientationWall() != null) {
					return false;
				}
			} else if(!orientationWall.equals(other.getOrientationWall())) {
				return false;
			}
		}

		if(bookmark == null) {
			if(other.getBookmark() != null) {
				return false;
			}
		} else if(!bookmark.equals(other.getBookmark())) {
			return false;
		}

		if(bookmarkValid != other.isBookmarkValid()) {
			return false;
		}

		return true;
	}
}

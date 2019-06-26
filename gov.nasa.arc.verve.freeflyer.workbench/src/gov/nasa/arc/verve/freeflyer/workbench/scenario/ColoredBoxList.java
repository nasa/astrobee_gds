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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import java.util.ArrayList;
import java.util.List;

public class ColoredBoxList {
	private String type;
	private List<ColoredBox> coloredBoxes = new ArrayList<ColoredBox>();

	public ColoredBoxList() {
		// for json deserializing
	}

	public void setColoredBoxes(List<ColoredBox> boxes) {
		this.coloredBoxes = boxes;
	}
	
	public int size() {
		return coloredBoxes.size();
	}
	
	public List<ColoredBox> getColoredBoxes() {
		return coloredBoxes;
	}
	
	public void addColoredBox(ColoredBox box) {
		coloredBoxes.add(box);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ColoredBoxList: ");
		for(ColoredBox box : coloredBoxes) {
			sb.append(box.name + ", ");
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		for(ColoredBox box : coloredBoxes) {
			result = prime * result + ((box == null) ? 0 : box.hashCode());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof ColoredBoxList)) {
			return false;
		}
		ColoredBoxList other = (ColoredBoxList)o;
		
		if(type == null) {
			if(other.getType() != null) {
				return false;
			}
		} else if(!type.equals(other.getType())) {
			return false;
		}
		
		if(coloredBoxes.size() != other.size()) {
			return false;
		}
		
		List<ColoredBox> otherBoxes = other.getColoredBoxes();
		
		for(int i=0; i<coloredBoxes.size(); i++) {
			if(!coloredBoxes.get(i).equals(otherBoxes.get(i))){
				return false;
			}
		}
		return true;
	}

	public static class ColoredBox {
		protected String name;
		protected float[] position;
		protected float[] color;
		
		public ColoredBox() {}
		
		public ColoredBox(String name, float[] position, float[] color) {
			this.name = name;
			this.position = position;
			this.color = color;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name + ", ");
			sb.append("position=");
			sb.append(arrayAsString(position));
			sb.append(", color=");
			sb.append(arrayAsString(color));
			return sb.toString();
		}

		private String arrayAsString(float[] arr) {
			StringBuilder sb = new StringBuilder("[");
			for(int i=0; i<arr.length-1; i++) {
				sb.append(arr[i] + ", ");
			}
			sb.append(arr[arr.length-1] + "]");
		
			return sb.toString();
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public float[] getPosition() {
			return position;
		}

		public void setPosition(float[] position) {
			this.position = position;
		}

		public float[] getColor() {
			return color;
		}

		public void setColor(float[] color) {
			this.color = color;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			result = prime * result + ((color == null) ? 0 : color.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(!(o instanceof ColoredBox)) {
				return false;
			}
			ColoredBox other = (ColoredBox)o;
			
			if(!getName().equals(other.getName())) {
				return false;
			}
			
			float[] otherPosition = other.getPosition();
			if(position.length != otherPosition.length) {
				return false;
			}

			for(int i=0; i<position.length; i++) {
				if(position[i] != otherPosition[i]) {
					return false;
				}
			}
			
			float[] otherColor = other.getColor();
			if(color.length != otherColor.length) {
				return false;
			}

			for(int i=0; i<color.length; i++) {
				if(color[i] != otherColor[i]) {
					return false;
				}
			}
			return true;
		}
	}
}

package tools;
import java.util.ArrayList;
import java.util.List;

public class Cell {

	
		private int value;
		private List<Integer> pv;
		
		public Cell(){
			this(0);
		}

		public Cell(int value) {
			this.value = value;
			this.pv = new ArrayList<>();
			if(value == 0) 
				{
				for(int i = 1; i <= 9; ++i) 
					pv.add(i);
				}
		}
		
		public Cell(int value, List<Integer> possibleValues) {
			this.value = value;
			this.pv = possibleValues;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public List<Integer> getPossibleValues() {
			return pv;
		}
		
		public void setPossibleValues(List<Integer> possibleValues) {
			this.pv = possibleValues;
		}

		public Cell copy() {
			return new Cell(getValue(), new ArrayList<Integer>(getPossibleValues()));
		}
	}

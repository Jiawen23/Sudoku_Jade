package tools;
import java.util.ArrayList;
import utilities.Constants;
import java.util.List;

public class Grid {
	private Cell[][] grid;
	
	public Grid(Cell[][] grid) {
		if(grid.length != Constants.GRID_BOUNDARY|| grid[0].length != Constants.GRID_BOUNDARY)
			throw new IllegalArgumentException("Grid has wrong dimensions");
		this.grid = copyGridArray(grid);
	}
	
	public Grid() {
		this.grid = new Cell[Constants.GRID_BOUNDARY][Constants.GRID_BOUNDARY]; 
	}

	private static Cell[][] copyGridArray(Cell[][] newcell) {
		if(newcell == null) return null;
		int lines = newcell.length;
		int columns = newcell[0].length;
		Cell[][] c = new Cell[lines][columns];
		for(int i = 0; i < lines; ++i) {
			c[i] = new Cell[columns];
			for(int j = 0; i < columns; ++j)
				c[i][j] = newcell[i][j].copy();
		}
		return c;
	}

	public Cell[][] getGrid() {
		return grid;
	}
	
	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}
	
	public List<Cell> getLine(int index) {
		List<Cell> line = new ArrayList<>();
		if(index > Constants.GRID_BOUNDARY) return line;
		for(int i = 0; i < grid.length; ++i) line.add(grid[index][i].copy());
		return line;
	}
	
	public List<Cell> getColumn(int index) {
		List<Cell> column = new ArrayList<>();
		if(index > 9) return column;
		for(int i = 0; i < grid.length; ++i) column.add(grid[i][index].copy());
		return column;
	}
	
	
	public List<Cell> getSquare(int index) {
		List<Cell> square = new ArrayList<>();
		if(index > Constants.GRID_BOUNDARY) return square;
		int lines = (index / 3) * 3;
		int columns = (index % 3) * 3;
		for(int i = lines; i < lines + 3; ++i) {
			for(int j = columns; j < columns + 3; ++j) {
				square.add(grid[i][j].copy());
			}
		}
		return square;
	}

	public void setLineWithIntersection(int index, List<Cell> line) {
		if(index > Constants.GRID_BOUNDARY) return;
		for(int i = 0; i < grid.length; ++i) {
			Cell newCell = line.get(i);
			if(newCell.getValue() == 0) newCell.setValue(grid[index][i].getValue());
			newCell.getPossibleValues().retainAll(grid[index][i].getPossibleValues());
			grid[index][i] = newCell;
		}
	}

	public void setColumnWithIntersection(int index, List<Cell> column) {
		if(index >Constants.GRID_BOUNDARY) return;
		for(int i = 0; i < grid.length; ++i) {
			Cell newCell = column.get(i);
			if(newCell.getValue() == 0) newCell.setValue(grid[i][index].getValue());
			newCell.getPossibleValues().retainAll(grid[i][index].getPossibleValues());
			grid[i][index] = newCell;
		}
	}
	

	public void setSquareWithIntersection(int index, List<Cell> square) {
		if(index > Constants.GRID_BOUNDARY) return;
		int lines = (index / 3) * 3;
		int columns = (index % 3) * 3;
		for(int i = lines; i < lines + 3; ++i) {
			for(int j = columns; j < columns + 3; ++j) {
				Cell newCell = square.get((i - lines) * 3 + (j - columns)).copy();
				if(newCell.getValue() == 0) newCell.setValue(grid[i][j].getValue());
				//Intersection
				newCell.getPossibleValues().retainAll(grid[i][j].getPossibleValues());
				grid[i][j] = newCell;
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder hSepBuild = new StringBuilder();
		for(int i = 0; i < grid.length; ++i) {
			hSepBuild.append("+---");
		}
		hSepBuild.append("+\n");
		String hSep = hSepBuild.toString();
		
		StringBuilder finalBuilder = new StringBuilder();
		for(int i = 0; i < grid.length; ++i) {
			finalBuilder.append(hSep);
			for(int j = 0; j < grid[i].length; ++j) {
				finalBuilder.append("| " + grid[i][j].getValue() + " ");
			}
			finalBuilder.append("|\n");
		}
		finalBuilder.append(hSep);
		return finalBuilder.toString();
	}
}

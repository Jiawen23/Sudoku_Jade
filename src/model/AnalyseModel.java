package model;

import java.util.ArrayList;
import java.util.List;

import tools.Cell;

public class AnalyseModel extends Model {
	private List<Cell> cells;
	
	public AnalyseModel() {
		this.cells = new ArrayList<>();
	}
	
	public AnalyseModel(List<Cell> cells) {
		this.cells = cells;
	}
	
	public List<Cell> getCells() {
		return cells;
	}
	
	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
}

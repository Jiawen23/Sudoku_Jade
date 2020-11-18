package agents;


import java.io.IOException;
import java.nio.file.Paths;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import model.AnalyseModel;
import model.Model;
import model.SimulationModel;
import tools.Cell;
import tools.Grid;

import java.util.List;
import java.util.Scanner;
import utilities.Constants;
import utilities.DF;

public class AgentEnvironnement extends Agent{
	private static final long serialVersionUID = 1L;
	private String filename;
	protected void setup() {
		System.out.println(getLocalName()+ "--> Installed");
		DF.registerAgent(this, Constants.ENVIRONMENT, Constants.ENVIRONMENT);
		addBehaviour(new EnvironmentBehaviour(this,Constants.GRID_1));// un grille pour tester
		}
	public class EnvironmentBehaviour extends SequentialBehaviour {
		private static final long serialVersionUID = 1L;
		private Grid grid;

		public EnvironmentBehaviour(Agent a, Integer[] cells) {
			super(a);
			this.grid = generateGridFromArray(cells);
			System.out.println("la grille initiale : ");
			System.out.println(grid);
			addSubBehaviour(new AdvanceSimulationBehaviour());
			addSubBehaviour(new SendEndNotificationBehaviour());
		}
		class AdvanceSimulationBehaviour extends Behaviour {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage message = myAgent.receive();
				if(message == null) block();
				else {
					switch(message.getPerformative()) {
					//Message de la simulation
					case ACLMessage.REQUEST:
						SimulationModel model = Model.deserialize(message.getContent(), SimulationModel.class);
						ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
						request.addReceiver(DF.findFirstAgent(myAgent, Constants.ANALYSE, model.getAgent().getName()));
						int index = model.getIndex();
						request.setContent(new AnalyseModel(getCellListFromIndex(index)).serialize());
						request.setConversationId(String.valueOf(model.getIndex()));
						getAgent().send(request);
						break;
					//Message de l'analyse
					case ACLMessage.INFORM:
						AnalyseModel result = Model.deserialize(message.getContent(), AnalyseModel.class);
						setCellListFromIndex(result.getCells(), Integer.parseInt(message.getConversationId()));
						//System.out.println(grid.toString());
						break;
					}
				}
			}

			@Override
			public boolean done() {
				return isGridResolved();
			}
			
			/*
			 * Récupération d'une entité de la grille.
			 * La convention de l'index est la suivante :
			 * - de 0 à GRID_SIZE - 1 : ligne ;
			 * - de GRID_SIZE à GRID_SIZE * 2 - 1 : colonne ;
			 * - de GRID_SIZE * 2 à GRID_SIZE * 3 - 1 : carré.
			 * @param index Indice de l'entité (0-27)
			 * @return Liste de cellules.
			 * @see Constants
			 */
			private List<Cell> getCellListFromIndex(int index) {
				if(index < Constants.GRID_BOUNDARY) return grid.getLine(index % Constants.GRID_BOUNDARY);
				else if(index < Constants.GRID_BOUNDARY * 2) return grid.getColumn(index % Constants.GRID_BOUNDARY);
				return grid.getSquare(index % Constants.GRID_BOUNDARY);
			}
			
			
			private void setCellListFromIndex(List<Cell> cells, int index) {
				if(index < Constants.GRID_BOUNDARY) grid.setLineWithIntersection(index % Constants.GRID_BOUNDARY, cells);
				else if(index < Constants.GRID_BOUNDARY * 2) grid.setColumnWithIntersection(index % Constants.GRID_BOUNDARY, cells);
				else grid.setSquareWithIntersection(index % Constants.GRID_BOUNDARY, cells);
			}
			
			
			private boolean isGridResolved() {
				Cell[][] cells = grid.getGrid();
				for(int i = 0; i < cells.length; ++i) {
					for(int j = 0; j < cells[0].length; ++j) {
						
						if(cells[i][j].getValue() == 0) return false;
					}
				}
				return true;
			}
		}
		
		
		class SendEndNotificationBehaviour extends OneShotBehaviour {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				System.out.println("La grille finale : ");
				System.out.println(grid);
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.addReceiver(DF.findFirstAgent(myAgent, Constants.SIMULATION, Constants.SIMULATION));
				myAgent.send(message);
			}
		}

		
		private Grid generateGridFromArray(Integer[] cells) {
			if(cells.length != Constants.GRID_BOUNDARY * Constants.GRID_BOUNDARY) {
				System.err.println("Wrong dimensions for Sudoku grid");
				System.exit(-1);
			}
			
			Cell[][] grid = new Cell[Constants.GRID_BOUNDARY][Constants.GRID_BOUNDARY]; 
			for(int i = 0; i < cells.length; ++i) {
				grid[i / Constants.GRID_BOUNDARY][i % Constants.GRID_BOUNDARY] = new Cell(cells[i]);
			}
			
			Grid returnGrid = new Grid();
			returnGrid.setGrid(grid);
			return returnGrid;
		}
	}
	
	protected void takeDown() {
		System.out.println(getLocalName()+"terminating.");
		}
}
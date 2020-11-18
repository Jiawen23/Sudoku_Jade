package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import model.AnalyseModel;
import model.Model;
import model.RegisterModel;
import tools.Cell;
import tools.Grid;
import utilities.Constants;
import utilities.DF;

public class AgentAnalyse extends Agent{
	private static final long serialVersionUID = 1L;
	private AID as;
	protected void setup() {
		System.out.println(getLocalName()+ "--> Installed");
		DF.registerAgent(this, Constants.ANALYSE, getLocalName());
		addBehaviour(new AnalyseBehaviour(this));
	}
	public class AnalyseBehaviour extends SequentialBehaviour {
		private static final long serialVersionUID = 1L;
		public AnalyseBehaviour(Agent a) {
			super(a);
			addSubBehaviour(new ParticipationBehaviour());
			addSubBehaviour(new SolutionBehaviour());
		}

		
		private class ParticipationBehaviour extends OneShotBehaviour {
			private static final long serialVersionUID = 1L;
			  public void action() {
				  ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE); 
				  msg.addReceiver(DF.findFirstAgent(myAgent, Constants.SIMULATION, Constants.SIMULATION));
				 // msg.setContent("Inserer"); 
				  RegisterModel model = new RegisterModel(getAgent().getLocalName());
					msg.setContent(model.serialize());
				  myAgent.send(msg);
			    
			} 
		}
		
		class SolutionBehaviour extends CyclicBehaviour {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage message = myAgent.receive(mt);
				if(message == null) block();
				else {
					AnalyseModel model = Model.deserialize(message.getContent(), AnalyseModel.class);//uses methods of deserialisation
				
					solveSudoku(model.getCells());
					ACLMessage answer = message.createReply();
					answer.setPerformative(ACLMessage.INFORM);
					answer.setContent(model.serialize());
					myAgent.send(answer);
				}
			}

			private boolean solveSudoku(List<Cell> cells) {
				
				Map<Integer, List<Integer>> counts = new HashMap<>();
				for(int i = 1; i <= 9; ++i) counts.put(i, new ArrayList<>());
				Map<List<Integer>, List<Integer>> twoValuesCounts = new HashMap<>();
				for(Cell cell : cells) Collections.sort(cell.getPossibleValues());
				for(int i = 0; i < cells.size(); ++i) {
					Cell cell = cells.get(i);
					List<Integer> possibleValues = cell.getPossibleValues();
					if(firstAlgo(cell)) return true;
					if(secondAlgo(cell, cells)) return true;
					for(int possibleValue : possibleValues) counts.get(possibleValue).add(i);
					if(possibleValues.size() == 2) {
						if(!twoValuesCounts.containsKey(possibleValues)) twoValuesCounts.put(possibleValues, new ArrayList<>());
						twoValuesCounts.get(possibleValues).add(i);
					}
				}
					
					if(thirdAlgo(counts, cells)) return true;
					if(fourthAlgo(twoValuesCounts, cells)) return true;
					return false;
			}
			
			
			private boolean firstAlgo(Cell cell) {
				List<Integer> possibleValues = cell.getPossibleValues();
				//Si une seule valeur possible, elle est choisie
				if(possibleValues.size() == 1) {
					cell.setValue(possibleValues.get(0));
					possibleValues.clear();
					return true;
				}
				return false;
			}
			
			
			private boolean secondAlgo(Cell cell, List<Cell> cells) {
				boolean changed = false;
				if(cell.getPossibleValues().isEmpty()) {
					for(Cell otherCell : cells) changed |= otherCell.getPossibleValues().remove(Integer.valueOf(cell.getValue()));
				}
				return changed;
			}
			
			
			
			
			private boolean thirdAlgo(Map<Integer, List<Integer>> counts, List<Cell> cells) {
				for(Entry<Integer, List<Integer>> entry : counts.entrySet()) {
					if(entry.getValue().size() == 1) {
						Cell cellToUpdate = cells.get(entry.getValue().get(0));
						cellToUpdate.setValue(entry.getKey());
						cellToUpdate.getPossibleValues().clear();
						return true;
					}
				}
				return false;
			}
			
		
			 
			private boolean fourthAlgo(Map<List<Integer>, List<Integer>> counts, List<Cell> cells) {
				for(Entry<List<Integer>, List<Integer>> entry : counts.entrySet()) {
					if(entry.getValue().size() == 2) {
						for(int i = 0; i < cells.size(); ++i) {
							if(!entry.getValue().contains(i)) {
								cells.get(i).getPossibleValues().removeAll(entry.getKey());
							}
						}
						return true;
					}
				}
				return false;
			}
		}
	}
	
	protected void takeDown() {
		System.out.println(getLocalName()+"terminating.");
		}
	
}

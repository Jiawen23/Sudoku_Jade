package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.MessageQueue;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Model;
import model.RegisterModel;
import model.SimulationModel;
import utilities.Constants;
import utilities.Constants;
import utilities.DF;

public class AgentSimulation extends Agent{
	private static final long serialVersionUID = 1L;
	//ArrayList<AID> SenderAid;
	//AID ae;
	//long period;
	protected void setup() {
		System.out.println(getLocalName()+ "--> Installed");
		DF.registerAgent(this, Constants.SIMULATION, Constants.SIMULATION);
		addBehaviour(new SimulationBehaviour(this));
//		Object args = getArguments();
//		addBehaviour(new WaitforSouscriptions(27));
//		addBehaviour(new SendNotificationBehaviour(this,20));
	}
	public class SimulationBehaviour extends SequentialBehaviour{
		private static final long serialVersionUID = 1L;
		private Map<Integer, RegisterModel> agents;
		
		public SimulationBehaviour(Agent agent){
			super(agent);
			agents = new HashMap<>();
			addSubBehaviour(new WaitforSouscriptionsBehaviour(agent));
			addSubBehaviour(new PerformSimulationWrapper(ParallelBehaviour.WHEN_ALL));
		}
		
		
		public class PerformSimulationWrapper extends ParallelBehaviour {
			private static final long serialVersionUID = 1L;
			
			public PerformSimulationWrapper(int param) {
				super(param);
				TickerBehaviour simulation = new PerformSimulationBehaviour(myAgent, Constants.SIMULATION_FREQUENCY);
				this.addSubBehaviour(simulation);
				this.addSubBehaviour(new WaitEnvironmentBehaviour(getAgent(), simulation));
			}
		}
		
		
		public class WaitforSouscriptionsBehaviour extends Behaviour {
			private static final long serialVersionUID = 1L;
			private int counter = Constants.ANALYSE_AGENTS_NB;
			public WaitforSouscriptionsBehaviour(Agent agent) {
				super(agent);
			}
			
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE); 
				ACLMessage answer = myAgent.receive(mt);
				if(answer == null) block();
				else {
					RegisterModel model = Model.deserialize(answer.getContent(), RegisterModel.class);
					agents.put(--counter, model);
				}
			}

			@Override
			public boolean done() {
				return counter == 0;
			}
		}
		
		
		public class PerformSimulationBehaviour extends TickerBehaviour {
			
			AID environment;
			
			public PerformSimulationBehaviour(Agent a, long period) {
				super(a, period);
			}

			private static final long serialVersionUID = 1L;

			@Override
			public void onTick() {
				environment = DF.findFirstAgent(getAgent(), Constants.ENVIRONMENT, Constants.ENVIRONMENT);
				//Envoi des demandes aux 27 agents
				for(Entry<Integer, RegisterModel> mapEntry : agents.entrySet()) {
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					message.addReceiver(environment);
					message.setContent(new SimulationModel(mapEntry.getValue(), mapEntry.getKey()).serialize());
					getAgent().send(message);
				}
			}
		}
		
		
		public class WaitEnvironmentBehaviour extends Behaviour {
			private static final long serialVersionUID = 1L;
			
			
			private boolean simulationEnded;
			
			private TickerBehaviour simulation;

			public WaitEnvironmentBehaviour(Agent a, TickerBehaviour simulation) {
				super(a);
				this.simulation = simulation;
				this.simulationEnded = false;
			}

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
						
				ACLMessage notif = getAgent().receive(mt);
				if(notif == null) block();
				else {
					//On arrête la simulation
					System.out.println("Simulation ended, grid resolved!");
					simulation.stop();
					simulationEnded = true;
				}
			}

			@Override
			public boolean done() {
				return simulationEnded;
			}
		}
	}
}
	

//	private class WaitforSouscriptions extends Behaviour {
//		int i=0;
//		int nb;
//		private WaitforSouscriptions(int nb){
//			
//			this.nb = nb;
//		}
//		@Override
//		
//		public void action() {
//			ACLMessage message = receive();
//			if (message != null) {
//				SenderAid.add(message.getSender());
//				i++;
//			} else
//				block();
//		}
//
//		@Override
//		public boolean done() {
//			return i == nb;
//		}
//	 
//	}
//	public class PerformSimulationWrapper extends ParallelBehaviour {
//		
//		public PerformSimulationWrapper(int param) {
//			super(param);
//			TickerBehaviour simulation = new SendNotificationBehaviour(getAgent(), Constants.SIMULATION_FREQUENCY);
//			this.addSubBehaviour(simulation);
//			this.addSubBehaviour(new WaitEnvironmentBehaviour(getAgent(), simulation));
//		}
//	}
//	public class SendNotificationBehaviour extends TickerBehaviour {
//
//		private SendNotificationBehaviour(Agent a,long period) {
//			super(a,period);
//			
//		}
//
//		@Override
//		public void onTick(){
//			
//			ACLMessage message = new ACLMessage (ACLMessage.REQUEST);
//			ObjectMapper data = new ObjectMapper();
//			
//			try {
//				message.setContent(data.writeValueAsString(myAgent));
//				message.addReceiver(new AID("Sudoku", AID.ISLOCALNAME));
//				super.myAgent.send(message);
//				
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			} 
//		}
//
//	}
//	public class WaitEnvironmentBehaviour extends Behaviour {
//		private boolean simulationEnded;
//		private TickerBehaviour simulation;
//
//		public WaitEnvironmentBehaviour(Agent a, TickerBehaviour simulation) {
//			super(a);
//			this.simulation = simulation;
//			this.simulationEnded = false;
//		}
//
//		@Override
//		public void action() {
//			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
//					
//			ACLMessage notif = getAgent().receive(mt);
//			if(notif == null) block();
//			else {
//				System.out.println("Simulation ended, grid resolved!");
//				simulation.stop();
//				simulationEnded = true;
//			}
//		}
//
//		@Override
//		public boolean done() {
//			return simulationEnded;
//		}
//	}
//	protected void takeDown() {
//		System.out.println("AgentSimulation"+getAID().getName()+"terminating.");
//		try {
//			DFService.deregister(this);
//		} catch (FIPAException e) {
//			e.printStackTrace();
//		}
//		}
//	
//	
//}



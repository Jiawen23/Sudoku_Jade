package containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import utilities.Constants;
import agents.AgentAnalyse;
import agents.AgentEnvironnement;
import agents.AgentSimulation;

public class SudokuContainer {

	public static String SECOND_PROPERTIES_FILE ="/Users/zhujiawen/eclipse-workspace/td5/src/containers/properties2";
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p1= null;
		try{
		p1 = new ProfileImpl(SECOND_PROPERTIES_FILE);
		ContainerController cc = rt.createAgentContainer(p1);
		AgentController ac1=cc.createNewAgent("environment", "agents.AgentEnvironnement", null);
		ac1.start();
		AgentController ac2=cc.createNewAgent("simulation", "agents.AgentSimulation",  null);
		ac2.start();
		for(int i = 0; i < Constants.ANALYSE_AGENTS_NB; ++i)
		cc.createNewAgent("analyse" + i, "agents.AgentAnalyse", null).start();
		}
		catch(Exception e) {
			System.out.println("Unable to start auxiliary container.");
			e.printStackTrace();
	}

}
}
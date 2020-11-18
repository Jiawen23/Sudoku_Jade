package containers;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
public class MainContainer {
	public static String MAIN_PROPERTIES_FILE = "/Users/zhujiawen/eclipse-workspace/td5/src/containers/properties1";
	
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p= null;
		try{
		p = new ProfileImpl(MAIN_PROPERTIES_FILE);
		AgentContainer mc = rt.createMainContainer(p); 
		mc.start();
		}
		catch(Exception e) {
			System.out.println("Unable to start main container.");
			e.printStackTrace();
	}

}
}

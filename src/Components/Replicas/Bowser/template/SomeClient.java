package template;

import java.rmi.*;
import comps.*;
/**
 * This class represents the object client for a distributed
 * object of class SomeImpl, which implements the remote 
 * interface SomeInterface.
 */

public class SomeClient {
	public static void main(String args[]) {
		

		try {
			int RMIPort;         
			String hostName;
			int portNum = 25334;
			
			//~~~~~~Start the Servers~~~~~~~~//
		
			Server mtl = new Server("Montreal", portNum, "MTL");
			Server que = new Server("Quebec", portNum, "QUE");
			Server she = new Server("Sherbrooke", portNum, "SHE");

			

			
			FunctionManager MTL = new FunctionManager(mtl, new Clients("MTLA7779"));
			
			// invoke the remote method(s)
			
			MTL.addAppointments("MTLA251219","Dental", 99);
			String message = mtl.getRegistryURL();
			String message1 = que.getRegistryURL();
			String message2 = she.getServerName();

			System.out.println(message);
			System.out.println(message1);
			System.out.println(message2);
			MTL.addAppointments("MTLE251119", "Physician", 4);
			MTL.addAppointments("MTLA251119", "Dental", 5);
			MTL.addAppointments("MTLM251119", "Surgeon", 6);
			MTL.addAppointments("MTLM251119", "Surgeon", 6);
			MTL.addAppointments("MTLM251119", "Peanut", 6);

			
			MTL.getAppointmentSchedule("MTLP6767");
			// method2 can be invoked similarly
		} // end try 
		catch (Exception ex) {
			ex.printStackTrace( );
		} // end catch
	} //end main
	// Definition for other methods of the class, if any.
	
	
	public static void initServers() {
		try {
			int RMIPort;         
			String hostName;
			String portNum = "25334";
			// Code for obtaining hostName and RMI Registry port 
			// to be supplied.

			// Look up the remote object and cast its reference 
			// to the remote interface class -- replace "localhost"
			// with the appropriate host name of the remote object.
			
			//~~~~~~Start MTL Server~~~~~~~~//
			String registryURLMTL = 
					"rmi://localhost:" + portNum + "/MTL";  
			SomeInterface mtl =
					(SomeInterface)Naming.lookup(registryURLMTL);
			
			String registryURLQUE = 
					"rmi://localhost:" + portNum + "/QUE";  
			SomeInterface que =
					(SomeInterface)Naming.lookup(registryURLQUE);
			
			String registryURLSHE = 
					"rmi://localhost:" + portNum + "/SHE";  
			SomeInterface she =
					(SomeInterface)Naming.lookup(registryURLSHE);
			
			
			// invoke the remote method(s)
			
			
			String message = mtl.someMethod1();
			System.out.println(message);
			System.out.println(mtl.someMethod2(22));
			// method2 can be invoked similarly
		} // end try 
		catch (Exception ex) {
			ex.printStackTrace( );
		} // end catch
	}
}//end class

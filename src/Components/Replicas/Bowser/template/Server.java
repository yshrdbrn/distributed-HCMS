package template;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import comps.Clients;
import comps.Appointment;
/**
 * This class represents the object server for a distributed
 * object of class SomeImpl, which implements the remote 
 * interface SomeInterface.
 */

public class Server{
	 String registryURL, serverName;
     //HashMap<String, HashMap<String, Appointment>> appDatabase = new HashMap<>();      
	 HashMap<String, HashMap<String, Appointment>> appDatabase = new HashMap<String, HashMap<String,Appointment>>();
		//Create clients
		
	 
	
     
	public Server(String serverName, int portNum, String serverInitials) {
		try{   
	 		 // code for obtaining RMI port number value omitted
	         SomeImpl exportedObj = new SomeImpl();
	         startRegistry(portNum);
	         // register the object under the name "some"
	         registryURL = "rmi://localhost:" + portNum + "/"+ serverInitials;
	         Naming.rebind(registryURL, exportedObj);
	         System.out.println(serverInitials + " Server ready.");
	      }// end try
	      catch (Exception re) {
	         System.out.println(
	            "Exception in MTLServer.main: " + re);
	      } // end catch
		
		this.serverName = serverName;
		System.out.println(serverName + " Server is connected");
		
	}

	
	
	public HashMap<String, HashMap<String, Appointment>> getAppDatabase() {
		return appDatabase;
	}



	public void setAppDatabase(HashMap<String, HashMap<String, Appointment>> appDatabase) {
		this.appDatabase = appDatabase;
	}



	//Getters and Setters
	public String getRegistryURL() {
		return registryURL;
	}
	public void setRegistryURL(String registryURL) {
		this.registryURL = registryURL;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public static void initClients() {
		 ArrayList<Clients> clientList = new ArrayList<>();
			Clients john = new Clients("MTLA3443");
			Clients michael = new Clients("MTLP6767");
			Clients jacques = new Clients("QUEA1967");
			Clients marc = new Clients("QUEP9085");
			Clients mark = new Clients("SHEA5728");
			Clients marq = new Clients("SHEP6572");

			clientList.add(john);
			clientList.add(michael);
			clientList.add(jacques);
			clientList.add(marc);
			clientList.add(mark);
			clientList.add(marq);    
		 }	
	
	//Start registry 
	private static void startRegistry(int RMIPortNum)
		      throws RemoteException{
		      try {
		         Registry registry = LocateRegistry.getRegistry(RMIPortNum);
		         registry.list( );  
					// The above call will throw an exception
		         // if the registry does not already exist
		      }
		      catch (RemoteException ex) {
		         // No valid registry at that port.
		         System.out.println(
		            "RMI registry cannot be located at port " 
		            + RMIPortNum);
		         Registry registry = LocateRegistry.createRegistry(RMIPortNum);
		         System.out.println(
		            "RMI registry created at port " + RMIPortNum);
		      }
		   } // end startRegistry
	
	
	
	     
      


   
} // end class

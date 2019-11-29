package Components.Replicas.Bowser.template;

import java.rmi.*;
import java.rmi.server.*;

/**
 * This class implements the remote interface SomeInterface.
 */

public class SomeImpl extends UnicastRemoteObject
   implements SomeInterface {
  
   public SomeImpl() throws RemoteException {
      super( );
   }
  
   public String someMethod1( ) throws RemoteException {
	   return "Somemethod1";
   }

   public int someMethod2( ) throws RemoteException {
	   return 32;
   }

@Override
public int someMethod2(float someParameter) throws RemoteException {
	// TODO Auto-generated method stub
	return 0;
}

} //end class

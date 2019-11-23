import CORBA.Network.Server;
import CORBA.Network.ServerHelper;
import Components.FrontEnd.FrontEndCORBA;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Properties;

public class InitSystem {
    public static void main(String[] args) {
        try {
            // create and initialize the ORB
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialPort", "1050");
            props.put("org.omg.CORBA.ORBInitialHost", "localhost");
            ORB orb = ORB.init(args, props);

            // get reference to rootPOA & activate
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // create servant and register it with the ORB
            FrontEndCORBA frontEndCORBA = new FrontEndCORBA();

            // get object reference from the servant
            org.omg.CORBA.Object frontEndRef = rootPOA.servant_to_reference(frontEndCORBA);

            // and cast the reference to a CORBA reference
            Server serverRef = ServerHelper.narrow(frontEndRef);

            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            NameComponent[] path = ncRef.to_name("FrontEnd");
            ncRef.rebind(path, frontEndRef);

            System.out.println("Front End Started.");

            // wait for invocations from clients
            while (true) {
                orb.run();
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}

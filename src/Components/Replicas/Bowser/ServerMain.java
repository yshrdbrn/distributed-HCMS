package Components.Replicas.Bowser;

import CORBA.Network.Server;
import CORBA.Network.ServerHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class ServerMain {
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
            ArrayList<ServerInfo> serversInfo = getServersInfo();
            ArrayList<ServerWrapper> serverWrappers = new ArrayList<>();
            serverWrappers.add(new ServerWrapper("MTL", serversInfo));
            serverWrappers.add(new ServerWrapper("SHE", serversInfo));
            serverWrappers.add(new ServerWrapper("QUE", serversInfo));

            // get object reference from the servant
            ArrayList<Object> serverWrapperRefs = new ArrayList<>();
            for (ServerWrapper serverWrapper : serverWrappers) {
                serverWrapperRefs.add(rootPOA.servant_to_reference(serverWrapper));
            }

            // and cast the reference to a CORBA reference
            ArrayList<Server> serverRefs = new ArrayList<>();
            for (Object serverWrapperRef : serverWrapperRefs) {
                serverRefs.add(ServerHelper.narrow(serverWrapperRef));
            }

            // get the root naming context
            // NameService invokes the transient name service
            Object objRef = orb.resolve_initial_references("NameService");

            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            for (int i = 0; i < serverRefs.size(); i++) {
                NameComponent[] path = ncRef.to_name(serverWrappers.get(i).getServerName());
                ncRef.rebind(path, serverRefs.get(i));
            }

            System.out.println("Server Stared.");

            // wait for invocations from clients
            while (true) {
                orb.run();
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    private static ArrayList<ServerInfo> getServersInfo() throws UnknownHostException {
        ArrayList<ServerInfo> serversInfo = new ArrayList<>();
        serversInfo.add(new ServerInfo("MTL", InetAddress.getLocalHost(), 5600));
        serversInfo.add(new ServerInfo("SHE", InetAddress.getLocalHost(), 5700));
        serversInfo.add(new ServerInfo("QUE", InetAddress.getLocalHost(), 5800));
        return serversInfo;
    }
}

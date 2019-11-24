package Components.Client;

import CORBA.Network.Server;
import CORBA.Network.ServerHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class ClientMain {
    static String[] serverNames = {"MTL", "SHE", "QUE"};

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");
        ORB orb = ORB.init(args, props);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        Server pointOfContact = ServerHelper.narrow(ncRef.resolve_str("FrontEnd"));

        String userID = getUserID();
        Client client = new Client(pointOfContact, userID);
        client.start();
    }

    private static String getUserID() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your local user ID:");
        String localUserID;
        while (true) {
            localUserID = scanner.next();
            if (!localUserID.equals("-1"))
                break;
            System.out.println("User doesn't exist. Try again.");
        }
        return localUserID;
    }
}

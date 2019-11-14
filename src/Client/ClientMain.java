package Client;

import Network.Server;
import Network.ServerHelper;
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

        HashMap<String, Server> servers = new HashMap<>();
        for (String serverName : serverNames)
            servers.put(serverName, ServerHelper.narrow(ncRef.resolve_str(serverName)));

        Server userServer = getUserServer(serverNames, servers);
        String userID = getUserID(userServer);
        Client client = new Client(userServer, userID);
        client.start();
    }

    private static Server getUserServer(String[] serverNames, HashMap<String, Server> servers) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the server which your account is located in:");
        for (int i = 0; i < serverNames.length; i++)
            System.out.println((i+1) + ". " + serverNames[i]);

        // Select the server to connect to
        int choice;
        while (true) {
            choice = scanner.nextInt();
            if (choice >= 1 && choice <= serverNames.length)
                break;
            System.out.println("Error. Please select the one of the servers above.");
        }
        return servers.get(serverNames[choice - 1]);
    }

    private static String getUserID(Server userServer) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your local user ID:");
        String userID;
        while (true) {
            String localUserID = scanner.next();
            userID = userServer.GetFullID(localUserID);
            if (!userID.equals("-1"))
                break;
            System.out.println("User doesn't exist. Try again.");
        }
        return userID;
    }
}

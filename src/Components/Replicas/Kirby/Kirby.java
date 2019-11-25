package Components.Replicas.Kirby;

import Components.Replicas.Replica;
import Model.Network.Request;
import Model.Network.Response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Kirby extends Replica {
    private HashMap<String, ServerWrapper> serverWrappers;

    public Kirby(int plus) {
        ArrayList<ServerInfo> serversInfo = null;
        try {
            serversInfo = getServersInfo(plus);
        } catch (UnknownHostException e) {
            System.out.println("ERROR Initializing Kirby");
            e.printStackTrace();
        }
        serverWrappers = new HashMap<>();
        serverWrappers.put("MTL", new ServerWrapper("MTL", serversInfo));
        serverWrappers.put("SHE", new ServerWrapper("SHE", serversInfo));
        serverWrappers.put("QUE", new ServerWrapper("QUE", serversInfo));
    }

    private ArrayList<ServerInfo> getServersInfo(int plus) throws UnknownHostException {
        ArrayList<ServerInfo> serversInfo = new ArrayList<>();
        serversInfo.add(new ServerInfo("MTL", InetAddress.getLocalHost(), 5600 + plus));
        serversInfo.add(new ServerInfo("SHE", InetAddress.getLocalHost(), 5700 + plus));
        serversInfo.add(new ServerInfo("QUE", InetAddress.getLocalHost(), 5800 + plus));
        return serversInfo;
    }

    @Override
    public Response resolveRequest(Request request) {
        return serverWrappers.get(request.getUser().getCity()).resolveRequest(request);
    }
}

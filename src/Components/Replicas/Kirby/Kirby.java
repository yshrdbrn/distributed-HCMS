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

    public Kirby(int add) {
        ArrayList<ServerInfo> serversInfo = null;
        try {
            serversInfo = getServersInfo(add);
        } catch (UnknownHostException e) {
            System.out.println("ERROR Initializing Kirby");
            e.printStackTrace();
        }
        serverWrappers = new HashMap<>();
        serverWrappers.put("MTL", new ServerWrapper("MTL", serversInfo));
        serverWrappers.put("SHE", new ServerWrapper("SHE", serversInfo));
        serverWrappers.put("QUE", new ServerWrapper("QUE", serversInfo));
    }

    private ArrayList<ServerInfo> getServersInfo(int add) throws UnknownHostException {
        ArrayList<ServerInfo> serversInfo = new ArrayList<>();
        serversInfo.add(new ServerInfo("MTL", InetAddress.getLocalHost(), 5600 + add));
        serversInfo.add(new ServerInfo("SHE", InetAddress.getLocalHost(), 5700 + add));
        serversInfo.add(new ServerInfo("QUE", InetAddress.getLocalHost(), 5800 + add));
        return serversInfo;
    }

    @Override
    public Response resolveRequest(Request request) {
        return serverWrappers.get(request.getUser().getCity()).resolveRequest(request);
    }
}

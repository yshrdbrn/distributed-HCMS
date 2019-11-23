package Components.Replicas.Bowser;

import Model.Network.Request;
import Model.Network.Response;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

class UDPServer {
    private List<ServerInfo> serverInfoList;
    private LocalServer localServer;
    private String serverName;

    private Gson gson;

    UDPServer(String serverName, LocalServer localServer, List<ServerInfo> serverInfoList) {
        this.serverInfoList = serverInfoList;
        this.localServer = localServer;
        this.serverName = serverName;
        gson = new Gson();

        // Start the UDP server
        ServerInfo ownServerInfo = null;
        for (ServerInfo serverInfo : serverInfoList) {
            if (serverInfo.getName().equals(serverName)) {
                ownServerInfo = serverInfo;
                break;
            }
        }
        assert ownServerInfo != null;
        final int port = ownServerInfo.getPort();
        Runnable task = () -> startUDPServer(port);
        Thread thread = new Thread(task);
        thread.start();
    }

    //
    // Methods when acting as a server
    //

    private void startUDPServer(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[40 * 1024];
            System.out.println(serverName + " UDP server started on port " + port);
            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                Request request = gson.fromJson(getJSONFromNetwork(socket, incomingPacket), Request.class);
                Response response = localServer.handleRequest(request);
                sendJSONToNetwork(socket, incomingPacket.getAddress(), incomingPacket.getPort(), gson.toJson(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    // Methods when acting as a client
    //

    Response sendRequest(Request request) {
        try {
            switch (request.getRequestType()) {
                case LIST_APPOINTMENT_AVAILABILITY:
                case GET_APPOINTMENT_SCHEDULE:
                    ArrayList<Response> responses = new ArrayList<>();
                    for (ServerInfo serverInfo : serverInfoList) {
                        if (!serverInfo.getName().equals(serverName))
                            responses.add(sendRequestToServer(request, serverInfo));
                    }
                    assert responses.size() == serverInfoList.size() - 1;
                    for (int i = 1; i < responses.size(); i++) {
                        responses.get(0).getData().addAll(responses.get(i).getData());
                    }
                    return responses.get(0);

                default:
                    for (ServerInfo serverInfo : serverInfoList) {
                        if (serverInfo.getName().equals(request.getAppointmentID().getCity()))
                            return sendRequestToServer(request, serverInfo);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Execution should never reach this point!!
        assert false;
        return null;
    }

    private Response sendRequestToServer(Request request, ServerInfo serverInfo) throws IOException {
        // Send Request
        DatagramSocket socket = new DatagramSocket();
        sendJSONToNetwork(socket, serverInfo.getAddress(), serverInfo.getPort(), gson.toJson(request));

        // Receive Response
        byte[] incomingData = new byte[40 * 1024];
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        return gson.fromJson(getJSONFromNetwork(socket, incomingPacket), Response.class);
    }

    //
    // Utility Methods
    //

    private String getJSONFromNetwork(DatagramSocket socket, DatagramPacket incomingPacket) throws IOException {
        socket.receive(incomingPacket);
        return new String(incomingPacket.getData(), 0, incomingPacket.getLength());
    }

    private void sendJSONToNetwork(DatagramSocket socket, InetAddress address, int port, String json) throws IOException {
        byte[] data = json.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
        socket.send(sendPacket);
    }
}

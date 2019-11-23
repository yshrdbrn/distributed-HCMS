package Components.Replicas.Bowser;

import java.net.InetAddress;

class ServerInfo {
    private String name;
    private InetAddress address;
    private int port;

    ServerInfo(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    String getName() {
        return name;
    }

    InetAddress getAddress() {
        return address;
    }

    int getPort() {
        return port;
    }
}

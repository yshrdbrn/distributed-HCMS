package Components;

import Config.ComponentConfig;
import Model.Network.Request;
import Model.Network.Response;
import Networking.CustomPacket;
import Networking.CustomPacketType;
import Networking.ReliablePacketHandler;

public abstract class Component {
    private ComponentConfig config;
    private int packetIDCounter;
    protected ReliablePacketHandler packetHandler;

    public Component(ComponentConfig config) {
        this.config = config;
        packetIDCounter = 0;
        this.packetHandler = new ReliablePacketHandler(this);
    }

    protected CustomPacket initRequestPacket(Request request) {
        return new CustomPacket(config, packetIDCounter++, CustomPacketType.REQUEST, request);
    }

    protected CustomPacket initResponsePacket(Response response) {
        return new CustomPacket(config, packetIDCounter++, CustomPacketType.RESPONSE, response);
    }

    protected CustomPacket initFaultPacket() {
        return new CustomPacket(config, packetIDCounter++, CustomPacketType.FAULT);
    }

    protected CustomPacket initWantPacket(int packetNumber) {
        return new CustomPacket(config, packetIDCounter++, CustomPacketType.WANT_PACKET, packetNumber);
    }

    public abstract void handleCustomPacket(CustomPacket customPacket);

    //
    // The following two methods are used by ReliablePacketHandler class
    //
    public ComponentConfig getConfig() {
        return config;
    }

    public int generatePacketID() {
        return packetIDCounter++;
    }
}

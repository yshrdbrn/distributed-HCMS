package Networking;

import Config.ComponentConfig;
import Model.Network.Request;
import Model.Network.Response;

public class CustomPacket {
    ComponentConfig sender;
    int packetID;

    CustomPacketType type;
    int wantRequestNumber;
    Request request;
    Response response;

    public CustomPacket(ComponentConfig sender, int packetID, CustomPacketType type) {
        this.sender = sender;
        this.packetID = packetID;
        this.type = type;
    }

    public CustomPacket(ComponentConfig sender, int packetID, CustomPacketType type, int wantRequestNumber) {
        this.sender = sender;
        this.packetID = packetID;
        this.type = type;
        this.wantRequestNumber = wantRequestNumber;
    }

    public CustomPacket(ComponentConfig sender, int packetID, CustomPacketType type, Request request) {
        this.sender = sender;
        this.packetID = packetID;
        this.type = type;
        this.request = request;
    }

    public CustomPacket(ComponentConfig sender, int packetID, CustomPacketType type, Response response) {
        this.sender = sender;
        this.packetID = packetID;
        this.type = type;
        this.response = response;
    }

    public ComponentConfig getSender() {
        return sender;
    }

    public void setSender(ComponentConfig sender) {
        this.sender = sender;
    }

    public int getPacketID() {
        return packetID;
    }

    public void setPacketID(int packetID) {
        this.packetID = packetID;
    }

    public CustomPacketType getType() {
        return type;
    }

    public void setType(CustomPacketType type) {
        this.type = type;
    }

    public int getWantRequestNumber() {
        return wantRequestNumber;
    }

    public void setWantRequestNumber(int wantRequestNumber) {
        this.wantRequestNumber = wantRequestNumber;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

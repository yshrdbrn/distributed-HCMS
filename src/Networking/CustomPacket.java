package Networking;

import Model.Network.Request;
import Model.Network.Response;

public class CustomPacket {
    String sender;
    int packetID;

    CustomPacketType type;
    int wantRequestNumber;
    Request request;
    Response response;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
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

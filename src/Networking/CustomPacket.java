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
}

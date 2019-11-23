package Components.Sequencer;

import Components.Handler;
import Model.Network.Request;
import Networking.CustomPacket;
import java.util.ArrayList;


public class Sequencer implements Handler {
    private int counter;
    private ArrayList<CustomPacket> packetsReceived;
    private CustomPacket customPacket;

    public void packetReceived() {

    }

    private void labelizeRequest(Request request) {
        request.setLabel(++counter);
    }


    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        switch (customPacket.getType()) {
            case WANT_PACKET:
                //ReliableCustomPacket.send(CustomPacket)
                //TODO: send want packet
            case REQUEST:
                //ReliableCustomPacket.send(Request)
                //TODO: send request object
        }
    }
}

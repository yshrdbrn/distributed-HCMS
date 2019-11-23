package Components.Sequencer;

import Components.Handler;
import Model.Network.Request;

import java.util.ArrayList;

public class Sequencer implements Handler {
    private ArrayList<CustomPacket> packetsReceived;
    private int counter;

    public Sequencer(ArrayList<CustomPacket> packetsReceived, int counter) {
        this.packetsReceived = packetsReceived;
        this.counter = counter;
    }

    public void packetReceived() {

    }

    private void labelize(Request request) {
        request.setLabel(++counter);
    }


    @Override
    public void handleCustomerPacker(CustomPacket customPacket) {
        //TODO
    }
}

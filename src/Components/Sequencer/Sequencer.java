package Components.Sequencer;

import Components.Component;
import Config.ComponentConfig;
import Model.Network.Request;
import Networking.CustomPacket;
import Networking.ReliablePacketHandler;

import java.util.ArrayList;

public class Sequencer extends Component {
    private int counter;
    private ReliablePacketHandler packetHandler;

    public void main(String[] args) {
        packetHandler = new ReliablePacketHandler(this);
    }

    public Sequencer(ComponentConfig config) {
        super(config);
    }

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

package Components.Sequencer;

import Components.Component;
import Config.ComponentConfig;
import Config.SystemConfig;
import Model.Network.Request;
import Networking.CustomPacket;
import Networking.CustomPacketType;
import Networking.ReliablePacketHandler;

import java.util.ArrayList;

public class Sequencer extends Component {
    private int counter;
    private ReliablePacketHandler packetHandler;
    private ArrayList<Request> allRequestsReceived;
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

    private void sendToAllReplicas(CustomPacket customPacket) {
        Request request = customPacket.getRequest();
        labelizeRequest(request);
        allRequestsReceived.add(request);
        CustomPacket newCustomPacket = initRequestPacket(request);

        packetHandler.sendPacket(newCustomPacket, SystemConfig.Bowser);
        packetHandler.sendPacket(newCustomPacket, SystemConfig.Kirby);
        packetHandler.sendPacket(newCustomPacket, SystemConfig.Richter);
    }

    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        switch (customPacket.getType()) {
            case WANT_PACKET:
                //ReliableCustomPacket.send(CustomPacket)
                //TODO: send want packet
            case REQUEST:
                sendToAllReplicas(customPacket);
        }
    }
}

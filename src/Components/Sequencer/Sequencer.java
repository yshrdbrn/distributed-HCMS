package Components.Sequencer;

import Components.Component;
import Config.ComponentConfig;
import Config.SystemConfig;
import Model.Network.Request;
import Networking.CustomPacket;
import Networking.ReliablePacketHandler;

import java.util.ArrayList;

public class Sequencer extends Component {
    private int counter;
    private ArrayList<Request> allRequestsReceived;

    public static void main(String[] args) {
        Sequencer sequencer = new Sequencer();
    }

    public Sequencer() {
        super(SystemConfig.Sequencer);
    }

    private void labelizeRequest(Request request) {
        request.setLabel(++counter);
    }

    private void sendToAllReplicas(CustomPacket customPacket) {
        Request request = customPacket.getRequest();
        labelizeRequest(request);
        allRequestsReceived.add(request);

        CustomPacket newCustomPacket = initRequestPacket(request);
        for (ComponentConfig replicaManager: SystemConfig.ReplicaManagers)
            packetHandler.sendPacket(newCustomPacket, replicaManager);
    }

    private void sendWantRequest(CustomPacket customPacket) {
        Request wantRequest = allRequestsReceived.get(customPacket.getWantRequestNumber());
        if (wantRequest == null) assert false;
        CustomPacket newCustomPacket = initRequestPacket(wantRequest);
        packetHandler.sendPacket(newCustomPacket, customPacket.getSender());
    }


    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        switch (customPacket.getType()) {
            case WANT_PACKET:
                sendWantRequest(customPacket);
                break;
            case REQUEST:
                sendToAllReplicas(customPacket);
                break;
        }
    }
}

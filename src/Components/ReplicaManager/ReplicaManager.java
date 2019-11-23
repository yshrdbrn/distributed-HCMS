package Components.ReplicaManager;

import Components.Component;
import Config.ComponentConfig;
import Config.SystemConfig;
import Model.Network.Request;
import Model.Network.RequestComparator;
import Networking.CustomPacket;
import Networking.ReliablePacketHandler;

import java.util.PriorityQueue;

public class ReplicaManager extends Component {
    private PriorityQueue<Request> requestsQueue = new PriorityQueue<>(20, new RequestComparator());
    private Request lastHandledRequest = new Request();
    private int faultCounter = 0;

    public void main(String[] args) {
        packetHandler = new ReliablePacketHandler(this);
    }

    public ReplicaManager(ComponentConfig config) {
        super(config);
    }

    private void handleRequest(Request request) {

        while(!requestsQueue.isEmpty() && requestsQueue.peek().getLabel() - lastHandledRequest.getLabel() == 1) {
            lastHandledRequest = requestsQueue.poll();
            //send the request to the router
        }

        for(int i = lastHandledRequest.getLabel() + 1; !requestsQueue.isEmpty() && i < requestsQueue.peek().getLabel(); i++)
                packetHandler.sendPacket(initWantPacket(i), SystemConfig.Sequencer);

    }

    private void handleFaultState() {
        faultCounter++;
        if(faultCounter == 3) {
            //kill replica
        }
    }


    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        switch (customPacket.getType()) {
            case REQUEST:
                requestsQueue.add(customPacket.getRequest());
                handleRequest(customPacket.getRequest());
            case FAULT:
                handleFaultState();
        }
    }
}

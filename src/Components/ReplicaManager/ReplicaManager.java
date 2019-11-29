package Components.ReplicaManager;

import Components.Component;
import Components.Replicas.Kirby.Kirby;
import Components.Replicas.Replica;
import Components.Replicas.Richter.Richter;
import Config.ComponentConfig;
import Config.SystemConfig;
import Model.Network.Request;
import Model.Network.RequestComparator;
import Model.Network.Response;
import Networking.CustomPacket;

import java.util.PriorityQueue;
import java.util.Scanner;

public class ReplicaManager extends Component {
    private PriorityQueue<Request> requestsQueue = new PriorityQueue<>(50, new RequestComparator());
    private Request lastHandledRequest = new Request();
    private int faultCounter = 0;

    private Replica replica;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 for Kirby, 2 for Bowser, 3 for Richter:");
        int n = scanner.nextInt();

        ReplicaManager manager;
        switch (n) {
            case 1:
                manager = new ReplicaManager(SystemConfig.Kirby, n);
                break;
            case 2:
                manager = new ReplicaManager(SystemConfig.Bowser, n);
                break;
            case 3:
                manager = new ReplicaManager(SystemConfig.Richter, n);
                break;
        }
    }

    private ReplicaManager(ComponentConfig config, int n) {
        super(config);
        switch (n) {
            case 1:
                replica = new Kirby(0, config);
                break;
            case 2:
                replica = new Kirby(1000, config);
                break;
            case 3:
                replica = new Richter();
                break;
        }
        lastHandledRequest.setLabel(0);
    }

    private void handleRequest(Request request) {

        while(!requestsQueue.isEmpty() && requestsQueue.peek().getLabel() - lastHandledRequest.getLabel() == 1) {
            lastHandledRequest = requestsQueue.poll();
            //send the request to the router
            Response response = replica.resolveRequest(lastHandledRequest);
            packetHandler.sendPacket(initResponsePacket(response), SystemConfig.FrontEnd);
        }

        for(int i = lastHandledRequest.getLabel() + 1; !requestsQueue.isEmpty() && i < requestsQueue.peek().getLabel(); i++)
                packetHandler.sendPacket(initWantPacket(i), SystemConfig.Sequencer);

    }

    private void handleFaultState() {
        faultCounter++;
        if(faultCounter == 3) {
            replica = null;
            System.out.println("Replica has been terminated.");
        }
    }


    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        switch (customPacket.getType()) {
            case REQUEST:
                requestsQueue.add(customPacket.getRequest());
                handleRequest(customPacket.getRequest());
                break;
            case FAULT:
                handleFaultState();
                break;
        }
    }
}

package Components.FrontEnd;

import Components.Component;
import Config.ComponentConfig;
import Config.SystemConfig;
import Model.Network.Request;
import Model.Network.Response;
import Networking.CustomPacket;
import Networking.CustomPacketType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrontEnd extends Component {
    private final List<CustomPacket> responses;
    private int[] inconsistentResultCounter;

    FrontEnd(ComponentConfig config) {
        super(config);
        responses = Collections.synchronizedList(new ArrayList<>());
        inconsistentResultCounter = new int[3];
    }

    Response handleRequest(Request request) {
        responses.clear();
        packetHandler.sendPacket(initRequestPacket(request), SystemConfig.Sequencer);
        return processResponses();
    }

    private Response processResponses() {
        int counter = 0;
        while (counter < 10) { // Check if all responses are received for 10 times
            if (responses.size() == 3)
                break;
//            // TODO: Remove this part
//            // For test
//            if (responses.size() >= 1)
//                return responses.get(0).getResponse();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }

        checkIfAReplicaCrashed();
        updateInconsistentResultCounter();
        checkIfAResponseIsIncorrect();

        // Return the correct result
        for (int i = 0; i < responses.size(); i++) {
            if (inconsistentResultCounter[i] < 2)
                return responses.get(i).getResponse();
        }

        // should not reach this line
        assert false;
        return null;
    }

    private void checkIfAReplicaCrashed() {
        assert responses.size() > 1;

        // One replica crashed
        if (responses.size() != 3) {
            // Send Fault packet to the faulty replica manager
            for (ComponentConfig replicaManager: SystemConfig.ReplicaManagers) {
                boolean found = false;
                for (CustomPacket res : responses) {
                    if (res.getSender().equals(replicaManager)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Replica crashed!");
                    packetHandler.sendPacket(initFaultPacket(), replicaManager);
                }
            }
        }
    }

    private void updateInconsistentResultCounter() {
        for (int i = 0; i < 3; i++)
            inconsistentResultCounter[i] = 0;
    }

    /**
     * Checks if all responses are similar or not
     * This method uses the inconsistentResultCounter array to keep track of how many inconsistencies
     * each response has. The number of each index shows how many inconsistencies each response has comparing to other
     * responses.
     */
    private void checkIfAResponseIsIncorrect() {
        for (int i = 0; i < responses.size(); i++)
            for (int j = i + 1; j < responses.size(); j++)
                if (!responses.get(i).getResponse().equals(responses.get(j).getResponse())) {
                    inconsistentResultCounter[i]++;
                    inconsistentResultCounter[j]++;
                }
        System.out.println("Inconsistency array:");
        for (int i = 0; i < 3; i++)
            System.out.print(inconsistentResultCounter[i] + " ");
        System.out.println();
        for (int i = 0; i < responses.size(); i++)
            if (inconsistentResultCounter[i] > 1)
                packetHandler.sendPacket(initFaultPacket(), responses.get(i).getSender());
    }

    @Override
    public void handleCustomPacket(CustomPacket customPacket) {
        if (customPacket.getType() == CustomPacketType.RESPONSE) {
            responses.add(customPacket);
        } else {
            assert false;
        }
    }
}

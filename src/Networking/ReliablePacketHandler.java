package Networking;

import Components.Component;
import Config.ComponentConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.SQLOutput;
import java.util.HashSet;

public class ReliablePacketHandler {
    static class AckStatus {
        boolean ackReceived;

        AckStatus() {
            this.ackReceived = false;
        }
    }

    private Gson gson;

    private Component component;
    private final AckStatus ackStatus;
    private HashSet<CustomPacket> allReceivedPackets;

    public ReliablePacketHandler(Component component) {
        this.component = component;
        ackStatus = new AckStatus();
        gson = new Gson();
        allReceivedPackets = new HashSet<>();

        int port = component.getConfig().getPort();
        Runnable task = () -> startListening(port);
        Thread thread = new Thread(task);
        thread.start();
    }

    // Starts a UDP server on given port
    private void startListening(int port) {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[40 * 1024];
            System.out.println("Listener started on port " + port);
            while (true) {
                // Receive the packet
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(incomingPacket);
                String json = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                CustomPacket receivedPacket = gson.fromJson(json, CustomPacket.class);

                    System.out.println("Got Packet.");
                    System.out.println("Sender: " + receivedPacket.getSender().getName());
                    System.out.println("ID: " + receivedPacket.getPacketID());


                // If packet is not duplicated, add the packet to hashset
                if (isPacketDuplicated(receivedPacket))
                    continue;

                allReceivedPackets.add(receivedPacket);
                if (receivedPacket.getType() == CustomPacketType.ACK) {
                    synchronized (ackStatus) {
                        ackStatus.ackReceived = true;
                    }
                } else {
                    // Send ACK to sender
                    CustomPacket ackPacket = new CustomPacket(component.getConfig(), component.generatePacketID(), CustomPacketType.ACK);
                    sendPacketToNetwork(ackPacket, receivedPacket.getSender());

                    Runnable handlePacket = () -> component.handleCustomPacket(receivedPacket);
                    Thread thread = new Thread(handlePacket);
                    thread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(CustomPacket packet, ComponentConfig destination) {
        Runnable task = () -> sendPacketOtherThread(packet, destination);
        Thread thread = new Thread(task);
        thread.start();
    }

    private void sendPacketOtherThread(CustomPacket packet, ComponentConfig destination) {
        int counter = 0;
        synchronized (ackStatus) {
            ackStatus.ackReceived = false;
        }

        while (counter < 10) {
            System.out.println("Send");
            try {
                // Send packet to destination
                sendPacketToNetwork(packet, destination);

                // Check if the packet arrived
                counter++;
                Thread.sleep(500);
                System.out.println("Counter = " + counter);
                synchronized (ackStatus) {
                    if (ackStatus.ackReceived) {
                        System.out.println("Ack received");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPacketToNetwork(CustomPacket packet, ComponentConfig destination) {
        try {
            DatagramSocket socket = new DatagramSocket();
            String json = gson.toJson(packet);
            byte[] data = json.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, destination.getAddress(), destination.getPort());
            socket.send(sendPacket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPacketDuplicated(CustomPacket packet) {
        return allReceivedPackets.contains(packet);
    }
}

package Components;

import Networking.CustomPacket;

public interface Handler {
    public void handleCustomPacket(CustomPacket customPacket);
}

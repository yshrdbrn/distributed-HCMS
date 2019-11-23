package Config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class SystemConfig {

    public static ComponentConfig Bowser;
    public static ComponentConfig Kirby;
    public static ComponentConfig Richter;
    public static ComponentConfig Sequencer;
    public static ComponentConfig FrontEnd;

    static {
        try {
            Bowser = new ComponentConfig("Bowser", InetAddress.getLocalHost(), 2000);
            Kirby = new ComponentConfig("Kirby", InetAddress.getLocalHost(), 2100);
            Richter = new ComponentConfig("Richter", InetAddress.getLocalHost(), 2200);
            Sequencer = new ComponentConfig("Sequencer", InetAddress.getLocalHost(), 2300);
            FrontEnd = new ComponentConfig("FrontEnd", InetAddress.getLocalHost(), 2400);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

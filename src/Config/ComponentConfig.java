package Config;

import java.net.InetAddress;

public class ComponentConfig {
    private String name;
    private InetAddress address;
    private int port;

    // Default constructor only for GSON usage
    // Do not use this constructor in your code
    public ComponentConfig() {
    }

    public ComponentConfig(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentConfig that = (ComponentConfig) o;

        if (port != that.port) return false;
        if (!name.equals(that.name)) return false;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + port;
        return result;
    }
}

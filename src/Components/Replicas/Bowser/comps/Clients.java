package comps;
import java.io.Serializable;
import java.util.ArrayList;
public class Clients implements Serializable {
    private String ID;
    private String location;
    ArrayList<Appointment> apps = new ArrayList<>();
    
    public Clients(String ID) {
        this.ID = ID;
        this.location = setLocation();
        
    }

    public ArrayList<Appointment> getApps() {
		return apps;
	}

	public void addApps(Appointment appointment) {
		this.apps.add(appointment);
	}


	public String getLocation() {
        return location;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isAdmin() {
        return ID.charAt(3) == 'A';
    }

    private String setLocation() {
        switch (ID.substring(0,3)) {
            case "MTL":
                return "MTL";
            case "QUE":
                return "QUE";
            case "SHE":
                return "SHE";
            default:
                return null;
        }
    }

}

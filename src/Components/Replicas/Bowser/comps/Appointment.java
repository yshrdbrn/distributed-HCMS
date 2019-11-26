package comps;
import java.io.Serializable;
import java.util.ArrayList;
public class Appointment implements Serializable{

	String appointmentType;
    String ID;
    int maxCapacity,currentCapacity;

    private String location;
    private String appointmentTime;
    private String date;
	private ArrayList<Clients> patients;

    public Appointment(String appointmentType, String ID, int maxCapacity) {
        this.appointmentType = appointmentType;
        this.ID = ID;
        this.patients = new ArrayList<>();
        this.maxCapacity = maxCapacity;
        this.location = ID.substring(0, 3);
        this.appointmentTime = ID.substring(3);
        this.date = ID.substring(4);
        this.currentCapacity = maxCapacity - patients.size();
    }

    public Appointment(String appointmentType, String ID) {
        this.appointmentType = appointmentType;
        this.ID = ID;
        this.location = ID.substring(0, 3);
        this.appointmentTime = ID.substring(3);
        this.date = ID.substring(4);
        this.patients = new ArrayList<>();
        this.currentCapacity = maxCapacity - patients.size();
    }

	public Appointment(String appointmentType) {
        this.appointmentType = appointmentType;
        this.patients = new ArrayList<>();
        this.currentCapacity = maxCapacity - patients.size();
    }
 
	
	//Getters and Setters
    public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getCurrentCapacity() {
		return currentCapacity;
	}

	public void setCurrentCapacity(int currentCapacity) {
		this.currentCapacity = currentCapacity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<Clients> getPatients() {
        return patients;
    }

	public void setPatients(ArrayList<Clients> patients) {
		this.patients = patients;
	}
	 public void addPatient(Clients patient) {
	        patients.add(patient);
	        currentCapacity = maxCapacity - patients.size();
	    }

	    public void removePatient(Clients patient) {
	        patients.remove(patient);
	        currentCapacity = maxCapacity - patients.size();
	    }
	public boolean doesHaveCapacity() {
        return maxCapacity > patients.size();
    }

}

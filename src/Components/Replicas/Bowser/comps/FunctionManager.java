package Components.Replicas.Bowser.comps;
import java.util.ArrayList;
import java.util.HashMap;

import Components.Replicas.Bowser.template.Server;


public class FunctionManager {

	Server server;
	Clients client;
	public FunctionManager(Server tempServer, Clients tempClient) {
		this.server = tempServer;
		this.client = tempClient;
	}
	//Admin Functions
	
	//	How to populate it
	//	innerMap.put("SubKey", "InnerValue");
	//	outerMap.put("OuterKey", innerMap);
	public boolean addAppointments(String appointmentID, String appointmentType, int capacity) {
		try {
			if(!this.client.isAdmin()) {
				System.out.println("Client is not an admin");
				return false;

			}
			String tempString = parseAppointmentType(appointmentType);
			if(tempString == null) {
				System.out.println("Unrecognized appointmentType: " + appointmentType);
				return false;
			}
			
			Appointment app = new Appointment(tempString, appointmentID, capacity);
	

			
			 if(server.getAppDatabase().containsKey(tempString)) {
		            if(server.getAppDatabase().get(tempString).containsKey(app.getID())) {
		                System.out.println("There is already an appointment with this appointment ID");
		            	return false; 
		            } else {
		            	server.getAppDatabase().get(tempString).put(app.getID(), app);
		                System.out.println("Appointment has been successfully added");
		            	return true; 
		                }
		        } else {
		            HashMap<String, Appointment> newEntry = new HashMap<>();
		            newEntry.put(app.getID(), app);
		            server.getAppDatabase().put(tempString, newEntry);
		            System.out.println("Appointment has been successfully added");
		            return true;  
		        }
			
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LOG code needed ~~~~~~~~~~~~~~~~~~~~~~~~~//

            
            
		}catch(Exception e) {System.err.println("Exception in addAppointments on JJ's Server//Under FunctionManager: "+e);}
		System.out.println("addAppointment task failed");
		return false;
	}
	
	
	public boolean removeAppointments(String appointmentID, String appointmentType) {
		if(!this.client.isAdmin()) {
			System.out.println("Client is not an admin");
			return false;

		}
		String tempString = parseAppointmentType(appointmentType);
		if(tempString == null) {
			System.out.println("Unrecognized appointmentType: " + appointmentType);
			return false;
		}
		
		//Remove appointment and store possible clients in arraylist
		
		ArrayList<Clients> clients = new ArrayList<>();
		if(server.getAppDatabase().containsKey(tempString)) {
            if(server.getAppDatabase().get(tempString).containsKey(appointmentID)) {
                clients = server.getAppDatabase().get(appointmentType).get(appointmentID).getPatients();
                server.getAppDatabase().get(appointmentType).remove(appointmentID);
            }
		
		}
		if(clients.isEmpty()){
			System.out.println("Appointment removed successfully");
			return true;
		}
		
		HashMap<String, Appointment> appointmentHashMap = server.getAppDatabase().get(appointmentType);
        for (HashMap.Entry<String, Appointment> appointmentEntry : appointmentHashMap.entrySet()) {
            if (isInFutre(appointmentEntry.getKey(), appointmentID)) {
                if (appointmentEntry.getValue().doesHaveCapacity()) {
                    for(int i = 0; i < appointmentEntry.getValue().getCurrentCapacity() && clients.size() != 0; i++) {
                        appointmentEntry.getValue().addPatient(clients.get(clients.size() - 1));
                        clients.remove(clients.size() - 1);
                        clients.trimToSize();

                    }
                }
            }
          }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LOG code needed ~~~~~~~~~~~~~~~~~~~~~~~~~//

		System.out.println("Appointment removed successfully");
        return true;
	}
	
	public String listAppointmentAvailability(String appointmentType) {
		return null;
	}
	
	
	
	//Client Functions
	
	public boolean bookAppointment(String patientID, String appointmentID, String appointmentType) {
		
		
//		Server.initClients();
		String tempType = parseAppointmentType(appointmentType);

		Clients tempClient = new Clients(patientID);
		
		if(server.getAppDatabase().containsKey(tempType)) {
			
            if (server.getAppDatabase().get(tempType).containsKey(appointmentID)) {
            	
            	if (!server.getAppDatabase().get(appointmentType).get(appointmentID).getPatients().contains(tempClient)) {
            		
                    if (server.getAppDatabase().get(appointmentType).get(appointmentID).doesHaveCapacity()) {
                    	
                    	server.getAppDatabase().get(appointmentType).get(appointmentID).addPatient(tempClient);
                        System.out.println("Appointment has been successfully booked.");
                    	return true; 
                    	
                    } else {System.out.println( "There is no enough capacity for that appointment."); return false; }
                } else {System.out.println("There is already a patient with the same appointment type and ID."); return false; }
            } else {System.out.println("There is no appointment matching this appointment ID."); return false; }
        } else {System.out.println( "There is no appointment matching with this appointment type."); }
		
		return false;
		
		
	}	
	
	
	public String getAppointmentSchedule (String patientID) {
		
		Clients tempClient = new Clients(patientID);
		ArrayList<Appointment> apps = new ArrayList<>();
		
		
		for(HashMap.Entry<String, HashMap<String, Appointment>> db : server.getAppDatabase().entrySet()) {
            HashMap<String, Appointment> appointmentHashMap = db.getValue();
            for(HashMap.Entry<String, Appointment> appointments : appointmentHashMap.entrySet()) {
                
            	for(int i=0; i < appointments.getValue().getPatients().size();i++) {
            		for(Clients c : appointments.getValue().getPatients()) {
                        if(c.equals(tempClient)) apps.add(appointments.getValue());
                    }
                }
            }
        }

		if(apps.isEmpty()) {
			return "No appointments booked";
		}else {
			String ret = "Here are the appointment(s) that are booked: " + apps.toString();
			return ret;
		}
		
	}
	
	public boolean cancelAppointment(String patientID, String appointmentID) {
		String ourID = this.client.getID();
		if(ourID != patientID) {
			System.out.println("Only the person who booked the appointment may cancel it");
			return false;
		}
		
		
		Clients tempClient = new Clients(patientID);
		ArrayList<Appointment> apps = new ArrayList<>();
		
		
		for(HashMap.Entry<String, HashMap<String, Appointment>> db : server.getAppDatabase().entrySet()) {
            HashMap<String, Appointment> appointmentHashMap = db.getValue();
                
            	if(appointmentHashMap.containsKey(appointmentID)) {
            	
            		Appointment a = appointmentHashMap.get(appointmentID);
            		a.removePatient(tempClient);
            		System.out.println("Appointment cancelled");
            		return true;
            	}
            	
            
        }
		
		
		
		System.out.println("Cannot cancel Appointment");
		return false;
	}
	
	
	
	private static String parseAppointmentType(String type) {
		if(type == "dental"||type == "DENTAL" || type == "Dental")
			return "Dental";
		if(type == "physician"||type == "PHYSICIAN" || type == "Physician")
			return "Physician";
		if(type == "surgeon"||type == "SURGEON" || type == "Surgeon")
			return "Surgeon";
		return null;
	}
	
    private boolean isInFutre(String toAdd, String toRemove) {
        int toAddYear = Integer.parseInt(toAdd.substring(toAdd.length() - 2));
        int toAddMonth = Integer.parseInt(toAdd.substring(toAdd.length() - 4, toAdd.length() - 2));
        int toAddDay = Integer.parseInt(toAdd.substring(toAdd.length() - 6, toAdd.length() - 4));

        int toRemoveYear = Integer.parseInt(toRemove.substring(toRemove.length() - 2));
        int toRemoveMonth = Integer.parseInt(toRemove.substring(toRemove.length() - 4, toRemove.length() - 2));
        int toRemoveDay = Integer.parseInt(toRemove.substring(toRemove.length() - 6, toRemove.length() - 4));

        if(toAddYear < toRemoveYear) return false;
        else {
            if(toAddMonth < toRemoveMonth) return false;
            else {
                if(toAddDay < toRemoveDay) return false;
                else return true;
            }
        }
    }
    

    
}

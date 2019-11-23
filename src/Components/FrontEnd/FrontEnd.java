package Components.FrontEnd;

import Components.Component;
import Config.ComponentConfig;
import Model.Network.Request;
import Model.Network.Response;
import Networking.CustomPacket;

public class FrontEnd extends Component {
    FrontEnd(ComponentConfig config) {
        super(config);
    }

    Response AddAppointment(Request request) {
        return null;
    }

    Response RemoveAppointment(Request request) {
        return null;
    }

    Response ListAppointmentAvailability(Request request) {
        return null;
    }

    Response BookAppointment(Request request) {
        return null;
    }

    Response CancelAppointment(Request request) {
        return null;
    }

    Response SwapAppointment(Request request) {
        return null;
    }

    Response GetAppointmentSchedule(Request request) {
        return null;
    }

    String GetFullID(String id) {
        return null;
    }


    @Override
    public void handleCustomPacket(CustomPacket customPacket) {

    }
}

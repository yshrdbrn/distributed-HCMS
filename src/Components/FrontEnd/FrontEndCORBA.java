package Components.FrontEnd;

import CORBA.Network.Request;
import CORBA.Network.Response;
import CORBA.Network.ServerPOA;
import Config.SystemConfig;

public class FrontEndCORBA extends ServerPOA {
    private FrontEnd frontEnd;

    public FrontEndCORBA() {
        frontEnd = new FrontEnd(SystemConfig.FrontEnd);
    }

    @Override
    public Response AddAppointment(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response RemoveAppointment(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response ListAppointmentAvailability(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response BookAppointment(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response CancelAppointment(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response SwapAppointment(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }

    @Override
    public Response GetAppointmentSchedule(Request request) {
        return frontEnd.handleRequest(new Model.Network.Request(request)).ToNetworkResponse();
    }
}

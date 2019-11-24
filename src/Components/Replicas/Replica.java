package Components.Replicas;

import Model.Network.Request;
import Model.Network.Response;

public abstract class Replica {
    public abstract Response resolveRequest(Request request);
}

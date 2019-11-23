package Model.Network;

import java.util.Comparator;
public class RequestComparator implements Comparator<Request> {
    @Override
    public int compare(Request o1, Request o2) {
        if(o1.getLabel() > o2.getLabel()) {
            return 1;
        }

        if(o1.getLabel() < o2.getLabel()) {
            return -1;
        }

        return 0;
    }
}

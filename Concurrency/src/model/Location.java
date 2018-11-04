package model;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private int locationNr;
    private int nrOfOffices;
    private List<Office> offices;

    public Location(int locationNr, int nrOfOffices) {
        this.locationNr = locationNr;
        this.nrOfOffices = nrOfOffices;

        offices = new ArrayList<>();
        makeOffices();
    }

    private void makeOffices() {
        for (int i=0; i<nrOfOffices; i++) {
            offices.add(new Office(locationNr, i));
        }
    }

    public List<Office> getOffices() {
        return this.offices;
    }

    public int getLocationNr() {
        return this.locationNr;
    }

    @Override
    public String toString() {
        return "Location " + locationNr;
    }
}

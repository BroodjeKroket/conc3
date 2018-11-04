package model;

import akka.actor.ActorRef;

public class Office {

    private ActorRef customer;
    private ActorRef reservedBy;
    private int officeNr;
    private int locationNr;

    public Office(int locationNr, int officeNr) {
        this.officeNr = officeNr;
        this.locationNr = locationNr;

        customer = null;
        reservedBy = null;
    }

    public boolean isAvailable() {
        return !(reservedBy != null || customer != null);
    }

    public boolean isReservedBy(ActorRef actor) {
        return actor == reservedBy;
    }

    public ActorRef getCustomer() {
        return customer;
    }

    public void setCustomer(ActorRef customer) {
        this.customer = customer;
    }

    public ActorRef getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(ActorRef reservedBy) {
        this.reservedBy = reservedBy;
    }

    public int getOfficeNr() {
        return officeNr;
    }

    public int getLocationNr() {
        return locationNr;
    }

    @Override
    public String toString() {
        return "Office " + officeNr + " on location " + locationNr;
    }
}

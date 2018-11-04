import akka.actor.ActorRef;
import akka.actor.dsl.Creators;
import model.Location;
import model.Office;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    static public class LocationListRequest {} //Customer requests all locations

    static public class LocationList {
        public List<Location> locations = new ArrayList<>();
        public ActorRef customer;

        public LocationList(List<Location> locations, ActorRef customer) {
            this.customer = customer;
            this.locations = locations;
        }
    } //List of locations

    static public class OfficeListRequest {
        public Location location;
        public ActorRef customer;

        public OfficeListRequest(Location location, ActorRef customer) {
            this.location = location;
            this.customer = customer;
        }
    } //Customer requests all offices on a location

    static public class AvailableOfficeListRequest {
        public GetOfficeOnLocationRequest request;

        public AvailableOfficeListRequest(GetOfficeOnLocationRequest request) {
            this.request = request;
        }
    }

    static public class OfficeList {
        public List<Office> offices;
        public ActorRef customer;

        public OfficeList(List<Office> offices, ActorRef customer) {
            this.customer = customer;
            this.offices = offices;
        }
    } //List of offices

    static public class AvailableOfficeList {
        public List<Office> offices;

        public AvailableOfficeList(List<Office> offices) {
            this.offices = offices;
        }
    }

    static public class GetOfficeOnLocationRequest {
        public Office office;
        public Location location;
        public ActorRef customer;

        public GetOfficeOnLocationRequest(Office office, Location location, ActorRef customer) {
            this.office = office;
            this.location = location;
            this.customer = customer;
        }
    } //Customer want to reserve an office on a location

    static public class ReleaseOfficeOnLocation {
        public int officeNr;
        public int locationNr;

        public ReleaseOfficeOnLocation(int officeNr, int locationNr) {
            this.officeNr = officeNr;
            this.locationNr = locationNr;
        }
    } //Customer leaves an office on a location

    static public class DenyOffice {
        public int officeNr;
        public int locationNr;

        public  DenyOffice(int officeNr, int locationNr){
            this.officeNr = officeNr;
            this.locationNr = locationNr;
        }
    } //Customer decides he doesn't want the available office

    static public class AcceptOffice {} //Customer decides he wants the available office

    static public class WaitForOffice {
        public Office office;

        public WaitForOffice(Office office){
            this.office = office;
        }
    } //Customer wants to wait until the requested office becomes available

    static public class OfficeAvailable {
        public ActorRef customer;
        public Office office;

        public OfficeAvailable(ActorRef customer, Office office) {
            this.customer = customer;
            this.office = office;
        }
    } //notify customer the requested office is available

    static public class OfficeNotAvailable {
        public ActorRef customer;

        public OfficeNotAvailable(ActorRef customer){
            this.customer = customer;
        }
    } //notify customer the requested office is not available

    static public class PaymentRequest {
        public int amount;

        public PaymentRequest(int amount) {
            this.amount = amount;
        }
    } //notify customer he has to pay

    static public class LocationFull {
        public ActorRef customer;

        public LocationFull(ActorRef customer) {
            this.customer = customer;
        }
    } //notify the customer the chosen location has no free offices



}

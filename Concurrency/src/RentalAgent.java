import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.Office;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Coen Neefjes on 30-10-2018.
 */
public class RentalAgent extends AbstractActor{

    public static Props props(int rentalAgentNr, List<ActorRef> locationAgents) {
        return Props.create(RentalAgent.class, locationAgents);
    }

    private int rentalAgentNr;
    private List<ActorRef> locationAgents;
    private Map<ActorRef, Office> reservations;
    private Map<Office, ActorRef> waitingCustomers;

    public RentalAgent(int rentalAgentNr, List<ActorRef> locationAgents) {
        this.rentalAgentNr = rentalAgentNr;
        this.locationAgents = locationAgents;

        reservations = new HashMap<>();
        waitingCustomers = new HashMap<>();
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Messages.LocationList.class, locationList -> { // Message from superFlex
                    System.out.println(this.toString() + " redirects locationlist");
                    // Redirect this message to the customer
                    locationList.customer.tell(locationList, getSelf());
                })
                .match(Messages.OfficeListRequest.class, officeListRequest -> { // Message from customer
                    System.out.println(this.toString() + " redirects officeListRequest");
                    // Redirect this message to the locationAgent of the specified location
                    locationAgents.get(officeListRequest.location.getLocationNr()).tell(officeListRequest, getSelf());
                })
                .match(Messages.OfficeList.class, officeList -> { // Message from locationAgent
                    System.out.println(this.toString() + " redirects officeList");
                    // Redirect this message to the customer
                    officeList.customer.tell(officeList, getSelf());
                })
                .match(Messages.GetOfficeOnLocationRequest.class, message -> {
                    // Save request locally
                    reservations.put(getSender(), message.office);
                    // Redirect message to locationAgent
                    locationAgents.get(message.location.getLocationNr()).tell(message, getSelf());
                })
                .match(Messages.LocationFull.class, locationFull -> {
                    assert reservations.containsKey(locationFull.customer);
                    // Redirect this message to the customer
                    locationFull.customer.tell(locationFull, getSelf());
                })
                .match(Messages.OfficeAvailable.class, officeAvailable -> {
                    assert reservations.containsKey(officeAvailable.customer);
                    if (waitingCustomers.size() == 0) {
                        // Redirect this message to the customer
                        officeAvailable.customer.tell(officeAvailable, getSelf());
                    } else {
                        Office office = officeAvailable.office;
                        if (waitingCustomers.containsKey(office)){
                            ActorRef waitingCustomer = waitingCustomers.get(office);
                            reservations.get(officeAvailable.customer).setReservedBy(waitingCustomer);
                            reservations.remove(officeAvailable.customer);
                            reservations.put(waitingCustomer, office);
                            officeAvailable.customer.tell(new Messages.OfficeNotAvailable(officeAvailable.customer), getSelf());
                        }
                    }
                })
                .match(Messages.OfficeNotAvailable.class, officeNotAvailable -> {
                    officeNotAvailable.customer.tell(officeNotAvailable, getSelf());
                })
                .match(Messages.ReleaseOfficeOnLocation.class, releaseOfficeOnLocation -> {
                    getSender().tell(new Messages.PaymentRequest(1), getSelf());
                    locationAgents.get(releaseOfficeOnLocation.locationNr).tell(new Messages.ReleaseOfficeOnLocation(releaseOfficeOnLocation.officeNr, releaseOfficeOnLocation.locationNr), getSelf());
                })
                .match(Messages.DenyOffice.class, denyOffice -> {
                    locationAgents.get(denyOffice.locationNr).tell(new Messages.ReleaseOfficeOnLocation(denyOffice.officeNr, denyOffice.locationNr), getSelf());
                })
                .match(Messages.AcceptOffice.class, acceptOffice -> {

                })
                .match(Messages.WaitForOffice.class, waitForOffice -> {
                    waitingCustomers.put(waitForOffice.office, getSender());
                })
                .build();
    }

    @Override
    public String toString() {
        return "RentalAgent " + rentalAgentNr;
    }
}

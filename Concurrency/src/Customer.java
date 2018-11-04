import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.Location;
import model.Office;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Customer extends AbstractActor{

    public static Props props(int customerNr, ActorRef superFlex) {
        return Props.create(Customer.class, customerNr, superFlex);
    }

    private int customerNr;
    private int balance = 100;
    private ActorRef superFlex;
    private Location chosenLocation;
    private Office chosenOffice;

    public Customer (int customerNr, ActorRef superFlex) {
        this.customerNr = customerNr;
        this.superFlex = superFlex;
    }

    @Override
    public void preStart() throws Exception {
        // Ask for a list of all locations from superFlex
        superFlex.tell(new Messages.LocationListRequest(), getSelf());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public String toString() {
        return "Customer " + customerNr;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.LocationList.class, locationList -> {
                    assert chosenLocation == null && chosenOffice == null;
                    System.out.println(this.toString() + " received locationList");
                    // Choose location
                    chosenLocation = chooseLocation(locationList.locations);
                    // Tell rentalAgent we want an office on this location
                    getSender().tell(new Messages.OfficeListRequest(chosenLocation, getSelf()), getSelf());
                })
                .match(Messages.OfficeList.class, officeList -> {
                    assert chosenLocation != null && chosenOffice == null;
                    System.out.println(this.toString() + " received officeList");
                    // Choose office
                    chosenOffice = chooseOffice(officeList.offices);
                    // Tell the rentalAgent we want this office
                    getSender().tell(new Messages.GetOfficeOnLocationRequest(chosenOffice, chosenLocation, getSelf()), getSelf());
                })
                .match(Messages.LocationFull.class, locationFull -> {
                    assert chosenLocation != null && chosenOffice != null;
                    System.out.println(this.toString() + " received location full message");
                    startOver();
                })
                .match(Messages.OfficeAvailable.class, officeAvailable -> {
                    //with 20% chance denies the reservation
                    if (getChance() < 20){
                        System.out.println(this.toString() + " chose to deny the reservation");
                        getSender().tell(new Messages.DenyOffice(chosenOffice.getOfficeNr(), chosenLocation.getLocationNr()), getSelf());
                        startOver();
                    } else {
                        System.out.println(this.toString() + " accepted reservation");
                        getSender().tell(new Messages.AcceptOffice(), getSelf());
                        stayInOffice();
                    }
                })
                .match(Messages.OfficeNotAvailable.class, officeNotAvailable -> {
                    System.out.println("recieved office not available message");
                    // 50/% chance customer waits for the desired office
                    if (getChance() > 50){
                        System.out.println("customer chose too wait for office");
                        getSender().tell(new Messages.WaitForOffice(chosenOffice), getSelf());
                    }
                })
                .match(Messages.PaymentRequest.class, paymentRequest -> {
                    //updates the balance of the customer
                    System.out.println(this.toString() + " updating balance");
                    balance = balance - paymentRequest.amount;
                    startOver();
                })
                .build();
    }

    private Location chooseLocation(List<Location> locations) {
        Random random = new Random();
        return locations.get(random.nextInt(locations.size()));
    }

    private Office chooseOffice(List<Office> offices) {
        Random random = new Random();
        return offices.get(random.nextInt(offices.size()));
    }

    private int getChance(){
        Random random = new Random();
        return random.nextInt(100)+1;
    }

    private void startOver(){
        chosenLocation = null;
        superFlex.tell(new Messages.LocationListRequest(), getSelf());
    }

    private void stayInOffice(){
        //TODO make this work after certain amount of time
        System.out.println("customer stayed in office");
        getSender().tell(new Messages.ReleaseOfficeOnLocation(chosenOffice.getOfficeNr(), chosenOffice.getLocationNr()), getSelf());
    }

    public int getCustomerNr() {
        return this.customerNr;
    }

}
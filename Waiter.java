public class waiter {
    private String name;
    public Waiter(String name) { // constructor
        this.name = name;
        //test
    }

    //be able to seat customers    
    public void seatCustomers(Customer customer){
        customer.setSeat();
        System.out.println(name + " seated customers");
    }
    //be able to take orders    
    public void takeOrder(Customer customer){
        customer.eating();
        System.out.println(name + " took orders");
    }

    //be able to serve food    
    public void serveFood(Customer customer){
        customer.readyToPay();
        System.out.println(name + " served food");
    }

    //be able to make customer wait
    public void waitCustomer(Customer customer){
        customer.loseSatisfaction(10); //arbitary number for now
        System.out.println(name + " made customer wait");
    }

    }


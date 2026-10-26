public class Food {
    public enum state {
        preparing,
        ready,
        served
    }
    private preperation_units;

    public Food(int choice){
        if(choice==1) preperation_units=1;//salad
        if(choice==2) preperation_units=1;//burger
        if(choice==3) preperation_units=2;//pasta
    }

    public void cooking(){...}//decrease preperation units
}

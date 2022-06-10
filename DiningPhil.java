import java.util.Random;

public class DiningPhil {
    
    Philosopher[] philosophers;
    Fork[] forks;
    Thread[] threads;
    int number;

    public static void main(String[] args){

        DiningPhil obj = new DiningPhil();
        obj.init();
        obj.startThinkingEating();

    }

    public void init() {

        number = 5;

        philosophers = new Philosopher[number];
        forks = new Fork[number];
        threads = new Thread[number];

        for (int i = 0; i < number; i++) {
            philosophers[i] = new Philosopher(i + 1);
            forks[i] = new Fork(i + 1);
        }
    }

    public void startThinkingEating(){

        for (int i = 0; i < number; i++) {

            final int index = i;

            threads[i] = new Thread(new Runnable() {

                public void run() {
                    try {

                        philosophers[index].start(forks[index], forks[(index + 1) % (number)]);

                    } catch (InterruptedException e) {

                        e.printStackTrace();

                    }
                }
            });

            threads[i].start();
        }
    }

    public class Fork{

        private int forkID;
        private boolean status; //true when not in use

        Fork(int forkID) {
            this.forkID = forkID;
            this.status = true;
        }

        public synchronized void free() throws InterruptedException {
            status = true;
        }

        public boolean pick(int philosopherId) throws InterruptedException { //return true if pick is successful

            int counter = 0;
            int waitUntil = new Random().nextInt(2) + 3;
            System.out.println("Phil: " + philosopherId + " try to pick Fork: " + forkID + ", time: " + waitUntil + " sec");

            while(!status){

                Thread.sleep(1000);

                counter++;
                
                if (counter > waitUntil) {
                    return false;
                }

            }

            status = false;

            return true;

        }
    }

    public class Philosopher {

        private int philosopherId;
        private Fork left, right;

        public Philosopher(int philID){
            this.philosopherId = philID;
        }

        public void start(Fork left, Fork right) throws InterruptedException{

            this.left = left;
            this.right = right;

            while(true) {
                //eat();
                if(new Random().nextBoolean()){
                    eat();
                } else{
                    think();
                }
            }
        }

        public void think() throws InterruptedException{

            System.out.println("The Philosopher: " + philosopherId + " is now thinking.");
            Thread.sleep(new Random().nextInt(1000) + 100);

        }

        public void eat() throws InterruptedException{

            boolean rightPick = false;
            boolean leftPick = false;

            System.out.println("Phil: " + philosopherId + " stop think, want eat");

            leftPick = left.pick(philosopherId);

            if(!leftPick){  //unsuccessful attempt
                System.out.println("Phil: " + philosopherId + " failed to pickup left fork: " + left.forkID);
                return;
            }
            System.out.println(philosopherId + " successful pickup his left fork: " + left.forkID);

            rightPick = right.pick(philosopherId);

            if(!rightPick){
                System.out.println("Phil: " + philosopherId + " failed, he then return the left: " + left.forkID);
                left.free();
                return;
            }

            System.out.println("The Philosopher: " + philosopherId + " successfully taken the right fork.");

            int eattime = new Random().nextInt(2) + 3;

            System.out.println("The Philosopher: " + philosopherId + " is now eating: " + eattime + "sec");

            Thread.sleep(eattime * 1000);

            left.free();
            right.free();

            System.out.println("The Philosopher: " + philosopherId + " has stopped eating and freed the Forks." + left.forkID + ", " + right.forkID);
        }
    }

}
package ueb2;

public class Simulation {
    public static void main(String[] args) {
        int argsLength = 1;
        if (args.length != argsLength) {
            System.out.println("Expected " + argsLength + "arguments. Got " + args.length + " arguments!");
        }
        try {
            int n = Integer.parseInt(args[0]);
            Simulation simulation = new Simulation(n);
        } catch (Exception e) {
            System.err.println("Could not parse first argument as int!");
        }
    }

    private int n;
    private Actor[] actors;

    public Simulation(int n) {
        this.n = n;
        this.actors = new Actor[n];
        for (int i = 0; i < n; i++) {
            Actor a = new Actor("" + i, n);
            actors[i] = a;
        }
        Observer o = new Observer("observer", n);
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!o.terminated()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            o.sendTermination();
            System.out.println("Terminated!");
        }).start();
    }
}

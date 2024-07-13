package ueb2;

import org.oxoo2a.sim4da.Simulator;

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
            e.printStackTrace();
        }
    }

    private int n;
    private Actor[] actors;

    public Simulation(int n) {
        Simulator sim = Simulator.getInstance();
        this.n = n;
        this.actors = new Actor[n];
        for (int i = 0; i < n; i++) {
            Actor a = new Actor("" + i, n);
            actors[i] = a;
        }
        Observer o = new Observer("observer", n);
        sim.simulate();
        long startTimeMilliSeconds = System.currentTimeMillis();
        System.out.println("Simulating " + n + " actors sending firework messages...");
        new Thread(() -> {
            sim.awaitSimulationStart();
            while (!o.terminated()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("There are no more firework messages in the simulation.");
            o.sendTermination();
            System.out.println("Terminated actor message receivers.");
            sim.shutdown();
            long endTimeMilliSeconds = System.currentTimeMillis();
            long duration = endTimeMilliSeconds - startTimeMilliSeconds;
            System.out.println("Shutdown of the simulation after " + duration + " milliseconds!");
        }).start();
    }
}

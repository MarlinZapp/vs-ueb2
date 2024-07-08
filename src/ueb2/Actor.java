package ueb2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.UnknownNodeException;

public class Actor extends Node {
    private int n;
    private double p = 0.8;
    private ActorStatus status;

    private int[] messages;

    public Actor(String name, int n) {
        super(name);
        this.n = n;
        messages = new int[n];
        Arrays.fill(messages, 0);
        this.status = ActorStatus.ACTIVE;
        new Receiver().start();
        firework();
    }

    private void firework() {
        new Thread(() -> {
            try {
                Thread.sleep(new Random().nextInt(9) * 1000 + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                messages[name()]--;
            }
            Random r = new Random();
            int nFireworks = r.nextInt(n) + 1;
            Message m = new Message();
            m.add("type", "firework");
            getRandomUniqueInts(nFireworks, n).forEach(i -> {
                try {
                    if (i != Integer.parseInt(NodeName())) {
                        send(m, "" + i);
                        synchronized (this) {
                            messages[i]++;
                        }
                    }
                } catch (UnknownNodeException e) {
                    System.err.println(
                            "Error occured while sending message from actor " + this.NodeName() + " to actor " + i);
                    e.printStackTrace();
                }
            });
            this.status = ActorStatus.PASSIVE;
        }).start();
    }

    private class Receiver extends Thread {
        private boolean terminated = false;

        public Receiver() {
        }

        @Override
        public void run() {
            while (!terminated) {
                Message m = receive();
                handleMessage(m);
            }
        }

        private void handleMessage(Message m) {
            String type = m.query("type");
            if (type == null) {
                System.err.println("Found no type in message");
            } else if (type == "firework") {
                Random r = new Random();
                if (r.nextDouble() < p) {
                    status = ActorStatus.ACTIVE;
                    firework();
                } else {
                    synchronized (this) {
                        messages[name()]--;
                    }
                }
                p = p / 2;
            } else if (type == "status") {
                Message answer = new Message();
                StringBuilder answerString = new StringBuilder();
                for (int i = 0; i < n; i++) {
                    int outI;
                    synchronized (this) {
                        outI = messages[i];
                        messages[i] = 0;
                    }
                    if (outI != 0) {
                        answerString.append(i + ":" + outI + ",");
                    }
                }
                answer.add("type", "status_answer");
                answer.add("message_vector", answerString.toString());
                try {
                    send(answer, "observer");
                } catch (UnknownNodeException e) {
                    e.printStackTrace();
                }
            } else if (type == "terminate") {
                terminated = true;
            }
        }
    }

    // gets m random integers between 0 (inclusive) and n (exclusive)
    private static Set<Integer> getRandomUniqueInts(int m, int n) {
        Random random = new Random();
        Set<Integer> uniqueInts = new HashSet<>();

        // Check if it's possible to get m unique numbers in the range 0 to n
        if (m > n) {
            throw new IllegalArgumentException("Cannot generate more unique numbers than the size of the range.");
        }

        while (uniqueInts.size() < m) {
            int randInt = random.nextInt(n); // Generate a random number between 0 (inclusive) and n (exclusive)
            uniqueInts.add(randInt); // Add to the set (automatically handles duplicates)
        }

        return uniqueInts;
    }

    private int name() {
        return Integer.parseInt(NodeName());
    }
}

package ueb2;

import java.util.Arrays;

import org.oxoo2a.sim4da.Message;
import org.oxoo2a.sim4da.Node;
import org.oxoo2a.sim4da.UnknownNodeException;

public class Observer extends Node {

    private int n;

    private int[] controlVector;

    public Observer(String name, int n) {
        super(name);
        this.n = n;
        controlVector = new int[n];
        Arrays.fill(controlVector, 0);
    }

    public boolean terminated() {
        Message m = new Message();
        m.add("type", "status");
        for (int i = 0; i < n; i++) {
            try {
                send(m, "" + i);
            } catch (UnknownNodeException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < n; i++) {
            Message answer = receive();
            String type = answer.query("type");
            if (type == "status_answer") {
                String messageVector = answer.query("message_vector");
                for (String message : messageVector.split(",")) {
                    String[] parts = message.split(":");
                    if (parts.length != 2) {
                        // System.err.println("message_vector element can not be split in 2 parts: " + message);
                        continue;
                    } else {
                        int node = Integer.parseInt(parts[0]);
                        int value = Integer.parseInt(parts[1]);
                        // System.out.println(node + " send / receives: " + value);
                        controlVector[node] += value;
                    }
                }
            }
        }
        /*
         * for (int i = 0; i < n; i++) {
         * System.out.println(i + " : " + controlVector[i]);
         * }
         * System.out.println();
         */
        for (int i = 0; i < n; i++) {
            if (controlVector[i] != -1) {
                return false;
            }
        }
        return true;
    }

    public void sendTermination() {
        Message m = new Message();
        m.add("type", "terminate");
        for (int i = 0; i < n; i++) {
            try {
                send(m, "" + i);
            } catch (UnknownNodeException e) {
                e.printStackTrace();
            }
        }
    }
}

import java.io.*;
import java.net.*;
import java.lang.*;


public class Timestamp {
    private long send = 0;
    private long receive = 0;

    public Timestamp(long send) {
        this.send = send;
    }
    public Timestamp setReceive(long receive) {
        this.receive = receive;
        return this;
    }

    public void print() {
        System.out.println(" "+String.valueOf(send) + " " + String.valueOf(receive));
    }

    public Timestamp setFirst () {
        this.receive = this.receive - this.send;
        this.send = 0;
        return this;
    }

    public Timestamp add(Timestamp ts) {
        this.send = this.send + ts.send;
        this.receive = this.receive + ts.receive;
        return this;
    }

    public long getSend(){
        return this.send;
    }

    public long getReceive(){
        return this.receive;
    }
}
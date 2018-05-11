import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class TrafficEstimator {

    private static ConcurrentHashMap<Integer,Timestamp> map = new ConcurrentHashMap<Integer, Timestamp>();

    public static void mapPut(Integer key, Timestamp value){
        map.put(key, value);
    }

    public static Timestamp mapGet(Integer key){
        return map.get(key);
    }

    public static void main(String[] args) throws IOException {
        // BlackBox is started at localhost with default port 4444
        // also initialize the sink port for the TrafficSink to receive packets later
        String hostname = args[0];
        InetAddress addr = InetAddress.getByName(hostname); 
        int port = Integer.parseInt(args[1]);
        int sinkPort = 4445;

        // read the user input for given parameters N L r
        int N = Integer.parseInt(args[2]);
        int L = Integer.parseInt(args[3]);
        int r = Integer.parseInt(args[4]);

        // start the traffic generator to send packets to blackbox
        new TrafficGenerator(addr, port, N, L, r, sinkPort).start();

        // start the traffic sink to receive packets from blackbox and compute timestamps
        new TrafficSink(sinkPort,N, L, r).start();
    }
}
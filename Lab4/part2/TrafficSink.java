import java.io.*;
import java.net.*;

public class TrafficSink extends Thread{
    static private int port;
    static private int N;
    static private int L;
    static private int r;

    public TrafficSink (int sinkPort, int N, int L, int r) {
        this.port = sinkPort;
        this.N = N;
        this.L = L;
        this.r = r;
    }

    // byte arry to integer
    public static int fromByteArray(byte [] value, int start, int length) {
        int result = 0;
        for (int i = start; i< start+length; i++) {
            result = (result << 8) + (value[i] & 0xff);
        }
        return result;
    }

    public void run() {
        // output stream for writing file
        PrintStream pout = null;
        
        try {
            DatagramSocket socket = new DatagramSocket(port);
            // file output stream
            FileOutputStream fout =  new FileOutputStream("sinkOutput.txt");
            pout = new PrintStream (fout);
    
            // byte array based on the packet size
            long packetSizeInByte = L/N;
            byte[] buf = new byte[(int)packetSizeInByte];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            
            long lastReceiveTime = 0;
            while (true) {
                socket.receive(packet);
                            
                // get sequence number from packet
                int seqNumber = fromByteArray(packet.getData(), 2, 2);

                // calculate the time that takes to receive the packet
                long currentTime = System.nanoTime();
                long storeTime = currentTime - lastReceiveTime;
                lastReceiveTime = currentTime;
                storeTime = storeTime / 1000;
                // initialize the first packet because there is no previous lastReceiveTime
                if (seqNumber == 1)
                    storeTime = System.nanoTime() /1000;  

                // update the receive timestamp
                TrafficEstimator.mapPut(seqNumber, TrafficEstimator.mapGet(seqNumber).setReceive(storeTime));

                // stop when all packets are received
                if (seqNumber == N )
                    break;
            }

            // since the first packet is specially handled, need to update it
            TrafficEstimator.mapPut(1, TrafficEstimator.mapGet(1).setFirst());
            // update the timestamps to get the culmulative timestamps
            for (int i = 2; i <= N; i++) {
                Timestamp ts = TrafficEstimator.mapGet(i-1);
                TrafficEstimator.mapPut(i, TrafficEstimator.mapGet(i).add(ts));
            }

            // store the results in the file and print out
            for (int i = 1; i <= N; i++) {
                Timestamp ts = TrafficEstimator.mapGet(i);
                System.out.print(i+" ");
                ts.print();
                pout.println(i + "\t" +  ts.getSend() + "\t" + ts.getReceive()); 
            }

            long Bmax = 0;
            for (int i = 1; i <= N; i++) {
                long temp = 0;
                long receiveTime = TrafficEstimator.mapGet(i).getReceive();
                for (int j = 1; j <= N; j++){
                    long sendTime = TrafficEstimator.mapGet(j).getSend();
                    if (sendTime > receiveTime){
                        temp = (j - i)*(L/N)*8;
                        break;
                    }
                }
                if (Bmax < temp)
                    Bmax = temp;
            }
            System.out.println("The max backlog is " + Bmax + " bits");
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();   
        }
        catch (IOException e) {
            e.printStackTrace();   
        } finally {   
            pout.close();
        }
    }
}

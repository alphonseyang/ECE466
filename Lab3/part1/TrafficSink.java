import java.net.*;
import java.io.*;

public class TrafficSink {
  public static void main(String[] args) throws IOException {
    PrintStream pout = null;
    
    //get ip address 
    String receiver_hostname =  InetAddress.getLocalHost().getHostAddress();  
    System.out.println("Running at: "+receiver_hostname);
    
    //set port to the same as the traffic generator
    int port = 4445;
    try {
        port = Integer.parseInt(args[0]);
    }
    finally {
        System.out.println(port);    
        DatagramSocket socket = new DatagramSocket(port);
        byte[] buf = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        System.out.println("Waiting ..."); 
                
        FileOutputStream fout =  new FileOutputStream("TrafficSinkOutput.txt");
	    pout = new PrintStream (fout);
			
	    int SeqNo = 1;
	    long last_receive_time = 0;
        while (true) {
            socket.receive(packet);

            //time to record is the current received time minus the last recieved packet time
            long got_packet_time = System.nanoTime();
            long time_to_record = got_packet_time - last_receive_time;
            if (SeqNo == 1) time_to_record = 0; 
            last_receive_time = got_packet_time;
            
            //time in microseconds
            time_to_record = time_to_record/1000;
            System.out.println(SeqNo+ "\t"+  packet.getLength() + "\t" + time_to_record);
            pout.println(SeqNo+ "\t"+  packet.getLength() + "\t" + time_to_record); 
            SeqNo++;
        }
    }
  }
}

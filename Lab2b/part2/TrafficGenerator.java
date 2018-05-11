import java.lang.*;
import java.net.*;
import java.io.*; 
import java.util.*; 

class TrafficGenerator {  
	public static void main (String[] args) { 
		BufferedReader bis = null; 
		String currentLine = null; 
		ArrayList<Frame> frames = new ArrayList<Frame>();
		String hostName = "localhost";
		PrintStream pout = null;
		float tMs;
		int nPacket;
		int lBytes; 
		int port = 4444;

		
		try {  

			//process input arguments
			hostName = args[0];
	        InetAddress addr = InetAddress.getByName(hostName);	
			tMs = Float.parseFloat(args[1]);
			nPacket = Integer.parseInt(args[2]);
			lBytes = Integer.parseInt(args[3]);
			FileOutputStream outputfile =  new FileOutputStream("output.txt");
			pout = new PrintStream (outputfile);
			DatagramSocket socket = new DatagramSocket();
			
			//some initialization for time 
			int i = 0;
			int SeqNo = 1;
			long waitTime = (long) tMs*1000*1000;
			long lastStartTime = System.nanoTime();
			long lastRecordTime = 0;
			long currTime;
			
			while(true) {
				//send n packets btb for every T ms 
			    for (i = 0; i<nPacket; i++) {

			        byte [] packetData = new byte[lBytes];
			        DatagramPacket packet = new DatagramPacket(packetData, lBytes, addr, port); 
			        long currSendTime = System.nanoTime();
			        long timeToRecord = currSendTime - lastRecordTime;
			        if (SeqNo == 1) timeToRecord = 0; 
			        lastRecordTime = currSendTime;
			        //convert nano time to ms
                    timeToRecord = timeToRecord/1000;
			        pout.println(SeqNo+ "\t"+  timeToRecord + "\t" + lBytes);
			        socket.send(packet);
			        SeqNo++;
			    }
			    long sendTime = lastStartTime + waitTime;
			    //wait Tms until next send time 
			    do {
			        currTime = System.nanoTime();
			    } while ( currTime < sendTime );
			    lastStartTime = currTime;  
			}
			
			
			} catch (IOException e) {  
			System.out.println("IOException: " + e.getMessage());  
		} finally {  
			if (bis != null) { 
				try { 
					bis.close(); 
				} catch (IOException e) { 
					System.out.println("IOException: " +  e.getMessage());  
				} 
			} 
		}
	}  
}
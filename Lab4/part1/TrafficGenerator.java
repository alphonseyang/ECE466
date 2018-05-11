import java.io.*; 
import java.util.*; 
import java.net.*;
import java.lang.*;

class TrafficGenerator extends Thread {  
	static private InetAddress addr;
	static private int blackBoxPort;
	static private int N;
	static private int L;
	static private int r;
	static private int sinkPort;

	public TrafficGenerator (InetAddress addr, int port, int N, int L, int r, int sinkPort) {
		this.addr = addr;
		this.blackBoxPort = port;
		this.N = N;
		this.L = L;
		this.r = r;
		this.sinkPort = sinkPort;
		System.out.println(N +" packets");
		System.out.println(L +" bytes");
		System.out.println(r +" kbps");
	}

	public static byte[] toByteArray(int value) {
		byte[] result = new byte[4];
		result[3] = (byte) ((value >>> (8*0)) & 0xFF);
		result[2] = (byte) ((value >>> (8*1)) & 0xFF); 
		result[1] = (byte) ((value >>> (8*2)) & 0xFF); 
		result[0] = (byte) ((value >>> (8*3)) & 0xFF);
		return result;
	}

	public void run () { 
		try {  
			DatagramSocket socket = new DatagramSocket();

			// transform into the bytes per second
			long rInBps = r * 1000 / 8; 
			// calculate each packet size
			long packetSizeInByte = L/N; 
			// calculate the time interval
			long intervalInNanosec = (packetSizeInByte * 1000000000)/ rInBps; 
			System.out.println(packetSizeInByte + " bytes per packet");
			System.out.println(intervalInNanosec + " nanosecond interval");
			System.out.println(N +" packets");

			long lastSendTime = System.nanoTime();
			long timestamp = 0;

			for (int i = 1; i <= N; i++){
				byte [] data = new byte[(int)packetSizeInByte];

				//add sink port to first two bytes
				System.arraycopy(toByteArray(sinkPort), 2, data, 0, 2);

				//add seqno to the next two bytes
				System.arraycopy(toByteArray(i), 2, data, 2, 2);
				DatagramPacket packet = new DatagramPacket(data, (int)packetSizeInByte, addr, blackBoxPort);
				
				// update the send time for the packet
				long sendTime = lastSendTime + intervalInNanosec;
				long currentTime =  System.nanoTime();
				while (currentTime < sendTime){
					currentTime = System.nanoTime();
				}
			    lastSendTime = currentTime;

			    // special handle for the first case
			    if (i == 1) {
			    	timestamp = System.nanoTime() /1000;
			    }
			    else {
			    	timestamp = lastSendTime - timestamp;
			    	timestamp = timestamp /1000;
			    }

			    // store the time stamp in the data strcuture
			    TrafficEstimator.mapPut(i, new Timestamp(timestamp));
			    timestamp = lastSendTime;
				socket.send(packet);
			}
		}
		catch (SocketException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}  
}




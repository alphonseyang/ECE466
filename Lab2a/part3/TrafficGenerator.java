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
		
		try {  
			//get host name from the internet
			hostName = args[0];
			InetAddress addr = InetAddress.getByName(hostName);	
			
			//read input and create output file
            File fin = new File("poisson3.data.txt"); 
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis);  
			
			// counter for the number of frames and the previous time
			int i = 0;
			float prevTime = 0;
			
			//store the frames with time and sequence number in order
			while ( (currentLine = bis.readLine()) != null) { 
				//read columns values
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); 
				String col2 = st.nextToken(); 
				String col3  = st.nextToken(); 
				
				//cast to corresponding data types
				int seqNo 	= Integer.parseInt(col1);
				float curTime 	= Float.parseFloat(col2);  
				int Fsize 	= Integer.parseInt(col3);
				
				//calculated the elapsed time
				float elTime = curTime - prevTime;
				long elTime_nano = (long) elTime*1000;
				prevTime = curTime;
			  
				//store packet
				byte [] data = new byte[Fsize];
				DatagramPacket packet = new DatagramPacket(data, Fsize, addr, 4444);
				Frame frame = new Frame(elTime_nano, seqNo, packet); 
				frames.add(i++, frame);
			} 

			//create a socket to send
			DatagramSocket socket = new DatagramSocket();
            long last_send_time = System.nanoTime();
			for (i=0; i< frames.size(); i++) {
			    Frame frame = frames.get(i);
			    long time_to_wait = frame.elTime;

			    //calculate the time and wait until the corresponding time
			    long send_at_time = last_send_time + time_to_wait;
			    long current_time = System.nanoTime();
			    while ( current_time < send_at_time ){
			    	current_time = System.nanoTime();
			    }

			    last_send_time = current_time;
			    socket.send(frame.packet);   
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




import java.io.*; 
import java.util.*; 
import java.net.*;
import java.lang.*;

class TrafficGeneratorEthernet {  
	public static void main (String[] args) { 
		
		BufferedReader bis = null; 
		String currentLine = null; 
		ArrayList<Frame> frames = new ArrayList<Frame>();
		String hostname = "localhost";
		
		try {  
			//get host name from the internet
			hostname = args[0];
			InetAddress addr = InetAddress.getByName(hostname);	
			
			//read input and create output file
            File fin = new File("BC-pAug89-small.TL"); 
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis); 
			
			// counter for the number of frames and the previous time
			int i = 0;
			float t1 = 0;
			
			//store the frames with time and sequence number in order
			while ( (currentLine = bis.readLine()) != null) { 
				//read columns values
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); 
				String col2 = st.nextToken(); 
				
				//cast to corresponding data types
				float t2 	= Float.parseFloat(col1);  
				int Fsize 	= Integer.parseInt(col2);
				
				//calculated the elapsed time
				float Etime = t2 - t1;
				long Etime_nano = (long) (Etime*1000*1000*1000);
				t1 = t2;
			  
				//store packet
				byte [] data = new byte[Fsize];
				DatagramPacket packet = new DatagramPacket(data, Fsize, addr, 4444);
				Frame frame = new Frame(Etime_nano, 1, packet); 
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
			// catch io errors from FileInputStream or readLine()  
			System.out.println("IOException: " + e.getMessage());  
		} finally {  
			// Close files   
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




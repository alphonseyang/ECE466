import java.io.*; 
import java.util.*; 
import java.net.*;
import java.lang.*;

class TrafficGeneratorVideo {  
	public static void main (String[] args) { 
		
		BufferedReader bis = null; 
		String currentLine = null; 
		String hostname = "localhost";
		int port = 4444;
		
		try {  
			//get host name from the internet
			hostname = args[0];
			InetAddress addr = InetAddress.getByName(hostname);	
			
			//read input and create output file
            File fin = new File("movietrace.data"); 
			FileReader fis = new FileReader(fin);  
			bis = new BufferedReader(fis);  
			
			//socket to send on
			DatagramSocket socket = new DatagramSocket();

			//intial varibales
			int i = 0;
			int max_packet_size = 64000;
			long last_send_time = System.nanoTime();
			
			float Etime = 33;  //33 microseconds
			long time_to_wait = (long) Etime*1000000; 

			while ( (currentLine = bis.readLine()) != null) { 
				//Parse line and break up into elements
				StringTokenizer st = new StringTokenizer(currentLine); 
				String col1 = st.nextToken(); 
				String col2 = st.nextToken(); 
				String col3  = st.nextToken(); 
				String col4  = st.nextToken(); 
				
				//Convert each element to desired data type 
				int SeqNo 	= Integer.parseInt(col1);
				float t2 	= Float.parseFloat(col2);  
				int Fsize 	= Integer.parseInt(col4);
				
				//calculate the time and wait until the corresponding time
			    long send_at_time = last_send_time + time_to_wait;
			    long current_time = System.nanoTime();
			    while ( current_time < send_at_time ){
			    	current_time = System.nanoTime();
			    }
			    last_send_time = current_time;

				//send the frame as packets up to max_packet_size
				while (Fsize > 0) {
					int cur_Fsize = Fsize % max_packet_size;
					if (cur_Fsize == 0 ) {
						cur_Fsize = max_packet_size;
					}
					byte [] data = new byte[cur_Fsize];
					DatagramPacket packet = new DatagramPacket(data, cur_Fsize, addr, port);
					socket.send(packet);
					System.out.println(SeqNo+" " +cur_Fsize);
					Fsize = Fsize - cur_Fsize;
				}
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




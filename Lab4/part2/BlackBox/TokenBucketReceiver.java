package BlackBox;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


/**
 * Listens on specified port for incoming packets.
 * Packets are sent out immediately if possible, and stored in buffer if not.
 */
public class TokenBucketReceiver implements Runnable
{
	// TokenBucketSender used to send packets immediately.
	private TokenBucketSender sender;
	// buffer used to store incoming packets 
	private Buffer buffer;
	// port on which packets are received
	private int port;
	// Bucket from which tokens are consumed when sending packets
	private Bucket bucket;	
	// delay of this network element (in ms)
	private double delay;
	
	/**
	 * Constructor.
	 * @param buffer Buffer to which packets are stored. 
	 * @param port Port on which to lister for packets.
	 * @param sender TokenBucketSender used to send packets.
	 * @param bucket Bucket from which tokens are consumed when sending packets.
	 * @param delay Delay for packets.
	 */
	public TokenBucketReceiver(Buffer buffer, int port, TokenBucketSender sender, Bucket bucket, double delay)
	{
		this.buffer = buffer;
		this.port = port;
		this.sender = sender;
		this.bucket = bucket;
		this.delay = delay/1000;
	}

	/**
	 * Listen on port and send out or store incoming packets to buffer.
	 * This method is invoked when starting a thread for this class.
	 */  
	public void run()
	{		
		DatagramSocket socket = null;
		try
		{
			socket = new DatagramSocket(port);
			
			// receive and put packets in buffer (or send immediately)
			while (true)
			{	
				byte[] buf = new byte[Buffer.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				socket.receive(packet);	
				int noTokens = bucket.getNoTokens();
				long bufferSize = buffer.getSizeInBytes();
						
				/*
				 * Process packet.
				 */
				
				// if buffer is empty, no packet is currently being sent,
				// there are enough tokens, and there is no delay
				// received packet should be sent immediately
				if (bufferSize == 0 
					&& !sender.sendingInProgress
					&& noTokens >= packet.getLength()*8
					&& delay==0)
				{
					bucket.removeTokens(packet.getLength()*8);
					sender.sendPacket(packet);
				}
				// else add packet to buffer if there is enough space
				else 
				{
					DatagramPacket toAdd = new DatagramPacket(packet.getData(), packet.getLength());
					toAdd.setAddress(packet.getAddress());
					toAdd.setPort(packet.getPort());
					if (buffer.addPacket(toAdd, delay) < 0)
					{
						System.err.println("Packet dropped.");
					}
				}
			}
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}

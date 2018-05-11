package TokenBucket;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * Removes and sends packets from buffer to a given address and port.
 */
public class TokenBucketSender implements Runnable
{	
	/**
	 * Indicates if TokenBucketSender is currently sending.
	 */
	public volatile boolean sendingInProgress;
	
	// destination port
	private int destPort;
	// destination address
	private InetAddress destAddress;
	// socket used to send packets
	private DatagramSocket socket;
	// buffer from which packets are sent
	private Buffer buffer;
	// Bucket from which tokens are removed when sending packets
	private Bucket bucket;
	
	/**
	 * Constructor. Creates socket.
	 * @param buffer Buffer from which packets are sent.
	 * @param destAddress IP address to which packets are sent.
	 * @param destPort Port to which packets are sent.
	 * @param bucket Bucket from which tokens are removed when sending packets.
	 */
	public TokenBucketSender(Buffer buffer, InetAddress destAddress, int destPort, Bucket bucket)
	{
		this.sendingInProgress = false;
		this.buffer = buffer;
		this.destAddress = destAddress;
		this.destPort = destPort;
		this.bucket = bucket;
		
		try
		{
			socket = new DatagramSocket();
		} 
		catch (SocketException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Send packet using socket.
	 * @param packet Packet to send.
	 */
	public synchronized void sendPacket(DatagramPacket packet)
	{
		try 
		{
			// change destination of packet (do forwarding)
			packet.setAddress(destAddress);
			packet.setPort(destPort);
	
			socket.send(packet);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Remove packets form buffer and send them.
	 * Packets are sent only when there are enough tokens in Bucket. 
	 * If there are not enough tokens sleeps until there are. 
	 * If buffer is empty sleeps until packet arrives to buffer.
	 * This method is invoked when starting a thread for this class.
	 */
	public void run()
	{
		while(true)
		{
			DatagramPacket packet = null;		
			// if buffer is non empty get first packet (without removing it)
			if ((packet = buffer.peek()) != null)
			{
				// if there are enough tokens
				if (bucket.getNoTokens() >= packet.getLength())
				{
					bucket.removeTokens(packet.getLength());
					// set sedingInProgress so that TokenBucketReceiver can't send packets
					sendingInProgress = true;
					buffer.removePacket();
					sendPacket(packet);
					// release sedingInProgress so that TokenBucketReceiver can send packets
					sendingInProgress = false;
				}
				else
				{
					// get how many nanoseconds before there are enough tokens to send packet
					long timeToWait = bucket.getWaitingTime(packet.getLength());
					try
					{
						// sleep until there are enough tokens
						Thread.sleep(timeToWait/1000000, (int)(timeToWait%1000000));
					} 
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// there are no packets in buffer to send. Wait for one to arrive to buffer.
			else
			{				
				try
				{
					synchronized (buffer)
					{
						buffer.wait();
					}
					
				} 
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}
}

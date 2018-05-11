package BlackBox;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Removes and sends packets from buffer to address they arrived from and 
 * port number given in first two bytes of data..
 */
public class TokenBucketSender implements Runnable
{	
	/**
	 * Indicates if TokenBucketSender is currently sending.
	 */
	public volatile boolean sendingInProgress;
	

	// socket used to send packets
	private DatagramSocket socket;
	// buffer from which packets are sent
	private Buffer buffer;
	// TokensBucket from which tokens are removed when sending packets
	private Bucket bucket;
	// port number used to send packets if no port number is specified in first two bytes of data
	private int defaultSenderPort = 4445;
	
	/**
	 * Constructor. Creates socket.
	 * @param buffer Buffer from which packets are sent.
	 * @param bucket Bucket from which tokens are removed when sending packets.
	 */
	public TokenBucketSender(Buffer buffer, Bucket bucket)
	{
		this.sendingInProgress = false;
		this.buffer = buffer;
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
			packet.setAddress(packet.getAddress());
			
			// set port number as default port number
			int portNumber = defaultSenderPort;
			
			// try to read port number from the packet data
			if (packet.getLength()>2)
			{
				portNumber = fromByteArray(packet.getData(), 0, 2);
				if (portNumber<=0)
				{
					portNumber = defaultSenderPort;
				}
			}
			packet.setPort(portNumber);
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
				// if this packet is ready to be sent
				long waitTime = buffer.getWaitTimeUntilDeparture();
				if (waitTime<=0)
				{
					// if there are enough tokens
					if (bucket.getNoTokens() >= packet.getLength()*8)
					{
						bucket.removeTokens(packet.getLength()*8);
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
						long timeToWait = bucket.getWaitingTime(packet.getLength()*8);
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
				else
				{
					// wait until departure time of first packet
					try
					{
						// sleep until there are enough tokens
						Thread.sleep(waitTime/1000000, (int)(waitTime%1000000));
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
	
	/**
	 * Converts a byte array to an integer.
	 * @param value		a byte array 
	 * @param start		start position in the byte array
	 * @param length	number of bytes to consider
	 * @return			the integer value
	 */
	public static int fromByteArray(byte [] value, int start, int length)
	{
		int Return = 0;
		for (int i=start; i< start+length; i++)
		{
			Return = (Return << 8) + (value[i] & 0xff);
		}
		return Return;
	}
}

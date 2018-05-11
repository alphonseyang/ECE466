package TokenBucket;


import java.net.InetAddress;
import java.net.UnknownHostException;



/**
 * Token bucket that listens on given port for incoming packets.
 * After applying shaping policy to packets they are sent to given address and port. 
 */
public class TokenBucket implements Runnable
{
	// buffer for incoming packets
	private Buffer buffer;
	// thread in charge of removing packets from buffer and sending them
	private TokenBucketSender sender;
	// thread in charge of receiving packets and putting them in buffer
	private TokenBucketReceiver receiver;
	// thread in charge of generating and holding tokens
	private Bucket bucket;
	
	
	/**	  
	 * Constructor. Creates a buffer, receiver, sender, and token generator class instances.
	 * @param inPort Port on which to listen for packets.
	 * @param outAddress IP address to which packets are sent.
	 * @param outPort Port to which packets are sent.
	 * @param maxPacketSize Maximum size of packet that will be received.
	 * @param bufferCapacity Capacity of buffer in bytes.
	 * @param bucketSize Number of tokens that token bucket can hold.
	 * @param bucketRate Token generating rate in tokens/sec.
	 * @param fileName Name of file token bucket uses to record packet arrivals.
	 */
	public TokenBucket(int inPort, String outAddress, int outPort, 
			int maxPacketSize, long bufferCapacity,
			int bucketSize, int bucketRate, String fileName)
	{
		if (bucketSize < maxPacketSize)
		{
			System.err.println("Bucket size should not be smaller than the maximum packet size!");
			System.err.println("Token bucket will be constructed with given parameters, but arrival of" + 
					"packet with size gratar than bucket size will prevent sending of any further packets.");
		}
		Buffer.MAX_PACKET_SIZE = maxPacketSize;
		buffer = new Buffer(bufferCapacity);
		bucket = new Bucket(bucketSize, bucketRate);
		try
		{
			InetAddress destAddress = InetAddress.getByName(outAddress);
			sender = new TokenBucketSender(buffer, destAddress, outPort, bucket);
		} 
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		receiver = new TokenBucketReceiver(buffer, inPort, sender, bucket, fileName);
	}
	
	/**
	 * Start token generator, receiver, and sender threads for token bucket.
	 * This method is invoked when starting a thread for this class. 
	 */
	public void run()
	{
		new Thread(bucket).start();
		new Thread(receiver).start();
		new Thread(sender).start();
	}
}

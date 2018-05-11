package BlackBox;

/**
 * BlackBox is a network element that applies the shaping policy.
 * After applying shaping policy to packets they are returned to sending address and given port.
 * BlackBox delays traffic by a constant amount of time TConstant (micro seconds), transmits traffic at a long-term rate
 * of RConstant (Mbps), and allows bursts of up to size bConstant (Bits).
 */
public class BlackBox implements Runnable
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
	 * Starts the BlackBox.
	 * This method is entry point for the BlackBox.
	 * This method takes one optional command line argument.
	 * This argument must be an integer and is the port number on which this BlackBox listens to for incoming packets.
	 * If no value is supplied default port number 4444 is used.
	 */
	public static void main(String[] args) 
	{
		/**
		 * Size of the token bucket (in Bits).
		 * Maximum allowed burst size.
		 */
		int bConstant = 11840;
		/**
		 * Token generation rate for the token bucket (in Mbps).
		 * Long-term transmission rate.
		 * Value of -1 is equal to infinity.
		 */
		double RConstant = -1;
		/**
		 * Constant traffic delay (in micro seconds).
		 */
		int TConstant = 1000;
		
		/**
		 * Port number on which BlackBox listens for incoming traffic.
		 */
		int portNumber = 4444;
		
		
		// first arg is port number on which to listen (if it exists)
		if (args.length>0)
		{
			portNumber = Integer.parseInt(args[0]);
		}
		
		// max packet size is 1480 bytes
		// buffer capacity is 10000*1480 bytes
		BlackBox bb = new BlackBox(portNumber, 1480, 10000*1480, bConstant, RConstant, TConstant);
		new Thread(bb).start();
		System.out.println("Starting BlackBox. Listening on port " + portNumber);
	}
	
	/**	  
	 * Constructor. Creates a buffer, receiver, sender, and token generator class instances.
	 * @param inPort Port on which to listen for packets.
	 * @param maxPacketSize Maximum size of packet that will be received (in bytes).
	 * @param bufferCapacity Capacity of buffer in bytes.
	 * @param bucketSize Number of bits that the token bucket can hold.
	 * @param bucketRate Token generating rate in bits/sec.
	 * @param delay Delay of the element (in milliseconds).
	 */
	public BlackBox(int inPort, 
			int maxPacketSize, long bufferCapacity,
			int bucketSize, double bucketRate, double delay)
	{
		if (bucketSize < maxPacketSize)
		{
			System.err.println("Bucket size should not be smaller than the maximum packet size!");
			System.err.println("Token bucket will be constructed with given parameters, but arrival of a " + 
					"packet with size gratar than bucket size will prevent sending of any further packets.");
		}
		Buffer.MAX_PACKET_SIZE = maxPacketSize;
		buffer = new Buffer(bufferCapacity);
		bucket = new Bucket(bucketSize, bucketRate);
		
		sender = new TokenBucketSender(buffer, bucket);
		receiver = new TokenBucketReceiver(buffer, inPort, sender, bucket, delay);
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

package PacketScheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;



/**
 * Packet scheduler that listens on given port for incoming packets.
 * Packets are sent out respecting given link capacity. 
 * Packets from each queue are sent out in first come first served basis.
 */
public class PacketScheduler implements Runnable
{
	// buffers for incoming packets
	private Buffer[] buffers;
	// thread in charge of removing packets from buffer and sending them
	private SchedulerSender sender;
	// thread in charge of receiving packets and putting them in buffer
	private SchedulerReceiver receiver;
	
	/**	  
	 * Constructor. Creates a buffers, receiver, and sender.
	 * @param inPort Port on which to listen for packets.
	 * @param outAddress IP address to which packets are sent.
	 * @param outPort Port to which packets are sent.
	 * @param linkCapacity Capacity of output link (in bps).
	 * @param numBuffer Number of buffers.
	 * @param maxPacketSize Maximum size of packet that will be received.
	 * @param bufferCapacities Array of buffer capacities in bytes. 
	 * @param fileName Name of file packet scheduler uses to record packet arrivals.
	 * 
	 */
	public PacketScheduler(int inPort, String outAddress, int outPort, long linkCapacity,
			int numBuffer,
			int maxPacketSize, long[] bufferCapacities, String fileName)
	{
		Buffer.MAX_PACKET_SIZE = maxPacketSize;
		if (numBuffer != bufferCapacities.length)
		{
			System.err.println("Number of buffers doesnt match number of capacities. Number of buffers ignored.");
		}
		
		// create buffers
		buffers = new Buffer[bufferCapacities.length];
		for (int i=0; i<bufferCapacities.length; i++)
		{
			buffers[i] = new Buffer(bufferCapacities[i]);
		}
		
		// create packet sender and receiver
		try
		{
			InetAddress destAddress = InetAddress.getByName(outAddress);
			sender = new SchedulerSender(buffers, destAddress, outPort, linkCapacity);
		} 
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		receiver = new SchedulerReceiver(buffers, inPort,  fileName);
	}
	
	/**
	 * Start receiver and sender threads for packet scheduler.
	 * This method is invoked when starting a thread for this class. 
	 */
	public void run()
	{
		new Thread(receiver).start();
		new Thread(sender).start();
	}
}

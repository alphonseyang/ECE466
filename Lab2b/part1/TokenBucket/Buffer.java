package TokenBucket;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * FIFO (First In First Out) buffer used to store incoming packets.
 * Capacity of buffer is specified in bytes (sum of lengths of all stored packets).
 * (Maximum possible capacity of buffer is Integer.MAX_VALUE packets.)
 * This buffer is thread-safe. All buffering methods achieve their effects atomically using internal locks
 * or other forms of concurrency control. 
 */
public class Buffer 
{
	/**
	 * Maximum size of packet in bytes.
	 * Default is 1480.
	 */
	public static int MAX_PACKET_SIZE = 1480;

	// capacity of buffer in bytes
	private long bufferCapacity;
	// size of buffer in bytes (current occupancy)
	private long sizeInBytes;
	// queue used to store packets
	private BlockingQueue<DatagramPacket> queue;
	
	
	/**
	 * Constructor.
	 * @param bufferCapacity Capacity of buffer in bytes.
	 */
	public Buffer(long bufferCapacity)
	{
		queue = new LinkedBlockingQueue<DatagramPacket>();
		sizeInBytes = 0L;
		this.bufferCapacity = bufferCapacity;
	}
	
	/**
	 * Add packet to buffer.
	 * If there is not enough space in buffer to add packet, packet is not added and -1 is returned.
	 * @param packet Packet to be added.
	 * @return Size of buffer in bytes <b>after</b> adding the packet (or -1).
	 */
	public synchronized long addPacket(DatagramPacket packet)
	{
		if (increaseSize(packet.getLength()))
		{
			queue.add(packet);
			// notify thread that is waiting on buffer (if any) that a packet has arrived
			this.notify();
			
			return sizeInBytes;
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * Remove a packet from the buffer.
	 * If there are no packets in buffer this methods returns null.
	 * @return Packet removed from queue (or null).
	 */
	public synchronized DatagramPacket removePacket()
	{
		DatagramPacket packet;
	
		// take a packet from buffer
		if ((packet = queue.poll()) != null)
		{
			decreaseSize(packet.getLength());
			return packet;
		}
		else
		{
			return null;
		}		
	}
	
	/**
	 * Get first packet from buffer without removing it.
	 * @return First packet form buffer, or null if buffer is empty.
	 */
	public synchronized DatagramPacket peek()
	{
		return queue.peek();
	}
	
	/**
	 * Return the size of queue (number of packets in queue).
	 * @return Size of queue.
	 */
	public int getSize()
	{
		return queue.size();
	}

	/**
	 * Get size of buffer in bytes.
	 * @return Current size of buffer in bytes.
	 */
	public synchronized long getSizeInBytes()
	{
		return sizeInBytes;
	}
	
	/**
	 * Increase the size of buffer. Used when adding packets to buffer.
	 * @param increaseBy Number of bytes to increase by.
	 * @return True if size can be increased, false if new size would be greater than capacity.
	 */
	private synchronized boolean increaseSize(long increaseBy)
	{
		if (sizeInBytes + increaseBy > bufferCapacity)
		{
			return false;
		}
		else
		{
			sizeInBytes += increaseBy;
			return true;
		}
	}
	
	/**
	 * Decrease the size of buffer. Used when removing packets to buffer.
	 * @param decreaseBy Number of bytes to increase by.
	 * @return True if size can be decreased, false if new size would be smaller than zero.
	 */
	private synchronized boolean decreaseSize(long decreaseBy)
	{
		if (sizeInBytes - decreaseBy < 0)
		{
			return false;
		}
		else
		{
			sizeInBytes -= decreaseBy;
			return true;
		}
	}
}



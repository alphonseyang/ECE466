import java.net.*;

public class Frame {
	public int seqNo;
	public long elTime;
	public DatagramPacket packet;

    public Frame(long elTime, int seqNo, DatagramPacket packet) {
    	this.elTime = elTime;
        this.seqNo = seqNo;
        this.packet = packet;      
    }						
}

package BlackBox;

/**
 * Token generator and bucket to store generated tokens.
 * For efficiency Bucket calculates number of tokens only when needed instead of
 * continually generating tokens and holding them in bucket.
 */
public class Bucket implements Runnable
{
	// bucket size (in bits)
	private int size;
	// time between generation of tokens (nanoseconds)
	private long tokenInterval;
	// last time number of tokens was updated
	private long lastTime;
	// number of tokens in bucket (at last update)
	// NOTE: this variable should never be used without previous call to updatNoTokens()
	private int noTokens;
	
	/**
	 * Constructor.
	 * @param size Size of bucket in bits.
	 * @param rate Token generating rate (bits/sec).
	 */
	public Bucket(int size, double rate)
	{
		this.size = size;
		if (rate<0)
		{
			// infinite rate
			this.tokenInterval = -1;
		}
		else
		{
			this.tokenInterval = (long) ((long) 1000l/rate);
		}
		this.noTokens = size;
	}
	
	/**
	 * Start generating tokens.
	 */
	public void run()
	{
		// when started there are size tokens, and starting time is last update time for
		// this number of tokens
		lastTime = System.nanoTime();
	}
	
	/**
	 * Update number of tokens in bucket.
	 * Using last update time this method calculates number of tokens that would have been generated 
	 * from last update until present time and adds it to number of tokens in bucket.
	 * Number of tokens in bucket doesn't exceed bucket size (excess tokens are discarded).
	 */
	private void updateNoTokens()
	{
		// current time
		long time = System.nanoTime();
		// time that has passed from last update
		long elapsedTime = time-lastTime;
		// add tokens that would have been generated in elapsed time
		noTokens += elapsedTime/tokenInterval;
		// set last update time to present time minus unused time
		// (Unused time is leftover time from elapsed time when integer number of tokens are generated.
		// This time will contribute to generation of first token at next update.)
		lastTime = time - elapsedTime%tokenInterval;
		// limit number of tokens to bucket size
		noTokens = (noTokens < size ? noTokens : size);	
	}
	
	/**
	 * Returns number of tokens in bucket.
	 * @return Number of tokens in bucket.
	 */
	public synchronized int getNoTokens()
	{
		if (tokenInterval < 0)
		{
			// with infinite rate bucket is always full
			return size;
		}
		updateNoTokens();
		return noTokens;
	}
	
	/**
	 * Removes specified number of tokens from bucket.
	 * @param noToRemove Number of tokens to remove.
	 * @return True if there was enough tokens in bucket to remove noToRemove tokens, else false.
	 */
	public synchronized boolean removeTokens(int noToRemove)
	{
		// with infinite rate number of tokens is always max
		if (tokenInterval < 0)
		{
			return true;
		}
		updateNoTokens();
		
		if (noTokens - noToRemove < 0)
		{
			return false;
		}
		else
		{
			noTokens -= noToRemove;
			return true;
		} 
	}
	
	/**
	 * Calculates waiting time (in nanoseconds) until bucket has tokensToWaitFor tokens.
	 * There is no guarantee that after this time there will be tokensToWaitFor tokens in bucket (another
	 * user of bucket can remove tokens from bucket during waiting time).
	 * @param tokensToWaitFor Number of tokens in bucket for which to get waiting time.
	 * @return Waiting time (in nanoseconds). 
	 */
	public synchronized long getWaitingTime(int tokensToWaitFor)
	{
		// with infinite rate there is no wait
		if (tokenInterval < 0)
		{
			return 0;
		}
		updateNoTokens();
		
		return (tokensToWaitFor-noTokens)*tokenInterval;
	}
}

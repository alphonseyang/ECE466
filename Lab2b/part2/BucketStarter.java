import TokenBucket.TokenBucket;

class BucketStarter {  
	public static void main (String[] args) { 
		// listen on port 4444, send to localhost:4445,
		// max. size of received packet is 1024 bytes,
		// buffer capacity is 100*1024 bytes,
		// token bucket has 10000 tokens, rate 5000 tokens/sec, and
		// records packet arrivals to bucket.txt).
		TokenBucket lb = new TokenBucket(4444, "localhost", 4445,
		1001, 10000*10000,10000, 8000000, "bucket.txt");
		new Thread(lb).start();
	}  
}

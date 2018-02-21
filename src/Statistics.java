import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// statistics data
public class Statistics
{
	public static AtomicInteger fetchAttempt = new AtomicInteger(0);
	// 2XX status code
	public static AtomicInteger fetchSucceed = new AtomicInteger(0);
	// 3XX 4XX 5XX
	public static AtomicInteger fetchFailorAbort = new AtomicInteger(0);
	
	public static AtomicLong totalURLExtract = new AtomicLong(0);
	// we need the size
	public static ConcurrentHashMap<String, Boolean> uniqueURL = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Boolean> uniqueWithinURL = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Boolean> uniqueWithoutURL = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<Integer, Integer> statusCode = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Integer> fileSize = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Integer> contentType = new ConcurrentHashMap<>();
	
	
	public static void statWrite() throws IOException
	{
		Writer writer = new BufferedWriter(new FileWriter("CrawlReport_foxnews.txt"));
		String data = "Name: Chengdong Liao\n"
				+ "USC ID: 6189689159\n"
				+ "News site crawled: foxnews.com\n"
				+ "Fetch Statistics\n"
				+ "================\n"
				+ "# fetches attempted: " + fetchAttempt.get() + "\n"
				+ "# fetches succeeded: " + fetchSucceed.get() + "\n"
				+ "# fetches aborted or failed: " + fetchFailorAbort.get() + "\n"
				+ "\n"
				+ "Outgoing URLs:\n"
				+ "==============\n"
				+ "Total URLs extracted: " + totalURLExtract.get() + "\n"
				+ "# unique URLs extracted: " + uniqueURL.size() + "\n"
				+ "# unique URLs within News Site: " + uniqueWithinURL.size() + "\n"
				+ "# unique URLs outside News Site: " + uniqueWithoutURL.size() + "\n"
				+ "\n"
				+ "Status Codes:\n"
				+ "==============\n"
				+ "200 OK: " + statusCode.getOrDefault(200, 0) + "\n"
				+ "301 Moved Permanently: " + statusCode.getOrDefault(301, 0) + "\n"
				+ "401 Unauthorized: " + statusCode.getOrDefault(401, 0) + "\n"
				+ "403 Forbidden: " + statusCode.getOrDefault(403, 0) + "\n"
				+ "404 Not Found: " + statusCode.getOrDefault(404, 0) + "\n"
				+ "\n"
				+ "File Sizes:\n"
				+ "==============\n"
				+ "< 1KB: " + fileSize.getOrDefault("<1KB", 0) + "\n"
				+ "1KB ~ <10KB: " + fileSize.getOrDefault("1-10KB", 0) + "\n"
				+ "10KB ~ <100KB: " + fileSize.getOrDefault("10-100KB", 0) + "\n"
				+ "100KB ~ <1MB: " + fileSize.getOrDefault("100-1MB", 0) + "\n"
				+ ">= 1MB: " + fileSize.getOrDefault(">=1MB", 0) + "\n"
				+ "\n"
				+ "Content Types:\n"
				+ "==============\n"
				+ "text/html: " + contentType.getOrDefault("text/html", 0) + "\n"
				+ "image/gif: " + contentType.getOrDefault("image/gif", 0) + "\n"
				+ "image/jpeg: " + contentType.getOrDefault("image/jpeg", 0) + "\n"
				+ "image/png: " + contentType.getOrDefault("image/png", 0) + "\n"
				+ "application/pdf: " + contentType.getOrDefault("application/pdf", 0) + "\n";
		
		writer.write(data);
		// don't forget this !
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		Statistics.statWrite();
		
	}
}

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;


// each thread cache some statistics data
// then flush data into file
public class MyCrawler extends WebCrawler
{
	
	// only html, doc, pdf, image: png,jpg,gif
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(html|doc|docx|pdf|png|jpeg|jpg|gif))$");
	private final int cacheLimit = 500;
	
	// I don't know why here initialize twice for threadLocal variables
	private final List<String> attemptCache = new ArrayList<>();
	private final List<String> visitCache = new ArrayList<>();
	private final List<String> allCache = new ArrayList<>();
	// here cannot use constructor
	
	@Override
	public void onStart()
	{
		System.out.println(getThread().getName() + " start...");
		super.onStart();
	}
	
	@Override
	public void onBeforeExit()
	{
		// flush statistics into file		
		CSVFileManager.writeAttemptFile(attemptCache);
		CSVFileManager.writeVisitFile(visitCache);
		CSVFileManager.writeAllURLFile(allCache);
		
		System.out.println(getThread().getName() + " end...");
	}
	
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription)
	{
		// attempt to fetch URLs
		String attemptURL = webUrl.getURL().toString();
		attemptCache.add(attemptURL + "," + statusCode + "\n");
		Statistics.statusCode.put(statusCode, Statistics.statusCode.getOrDefault(statusCode, 0) + 1);
		
		Statistics.fetchAttempt.incrementAndGet();
		if (statusCode >= 200 && statusCode < 300)
		{
			Statistics.fetchSucceed.incrementAndGet();
		}
		else if (statusCode >= 300)
		{
			Statistics.fetchFailorAbort.incrementAndGet();
		}
		
		// flush into file if reach size limitation
		if (attemptCache.size() >= cacheLimit)
		{
			CSVFileManager.writeAttemptFile(attemptCache);
			// clean cache
			attemptCache.clear();
		}
	}
	
	@Override
	// url is the outlinks in page!
	public boolean shouldVisit(Page page, WebURL webUrl)
	{

		String url = webUrl.getURL();
		// ==============
		Statistics.uniqueURL.put(url, true);
		
		// ==============
		
		// all urls here with content type
		StringBuilder res = new StringBuilder();
		
		res.append(url).append(",");
		// range
		String tmpUrl = url.toLowerCase();
		if (tmpUrl.startsWith("https://www.foxnews.com") || tmpUrl.startsWith("http://www.foxnews.com"))
		{
			Statistics.uniqueWithinURL.put(url, true);
			res.append("OK").append("\n");
		}
		else
		{
			Statistics.uniqueWithoutURL.put(url, true);
			res.append("N_OK").append("\n");
		}
		
		allCache.add(res.toString());
		if (allCache.size() >= cacheLimit * 20)
		{
			CSVFileManager.writeAllURLFile(allCache);
			allCache.clear();
		}
		
		// check link type???
		// de-duplicate
		String origin = page.getWebURL().getURL().toLowerCase();
		boolean ret = !origin.equals(tmpUrl)
				&& FILTERS.matcher(tmpUrl).matches()
				// for both https and http protocol
				&& (tmpUrl.startsWith("https://www.foxnews.com/") || tmpUrl.startsWith("http://www.foxnews.com/"));		
		return ret;
	}
	
	// successfully download
	@Override
	public void visit(Page page)
	{
		String url = page.getWebURL().getURL();
		
		// ==============
//	    System.out.println("URL: " + url);
		StringBuilder res = new StringBuilder();
		res.append(url).append(",");
		// byte size
		double kbSize = page.getContentData().length / 1024;
		res.append(kbSize).append(",");
		if (kbSize < 1.0)
		{
			Statistics.fileSize.put("<1KB", Statistics.fileSize.getOrDefault("<1KB", 0) + 1);
		}
		else if (kbSize >= 1.0 && kbSize < 10)
		{
			Statistics.fileSize.put("1-10KB", Statistics.fileSize.getOrDefault("1-10KB", 0) + 1);
		}
		else if (kbSize >= 10.0 && kbSize < 100)
		{
			Statistics.fileSize.put("10-100KB", Statistics.fileSize.getOrDefault("10-100KB", 0) + 1);
		}
		else if (kbSize >= 100.0 && kbSize < 1024)
		{
			Statistics.fileSize.put("100-1MB", Statistics.fileSize.getOrDefault("100-1MB", 0) + 1);
		}
		else
		{
			Statistics.fileSize.put(">=1MB", Statistics.fileSize.getOrDefault(">=1MB", 0) + 1);
		}
		
		
		int outGoingLinks = 0;
	    if (page.getParseData() instanceof HtmlParseData) 
	    {
	        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	        // outgoing links number
	        outGoingLinks = htmlParseData.getOutgoingUrls().size();
	    }
	    // BinaryParseData
	    else if (page.getParseData() instanceof BinaryParseData)
	    {
	    		BinaryParseData binaryParseData = (BinaryParseData) page.getParseData();
	    		// outgoing links number
	    		outGoingLinks = binaryParseData.getOutgoingUrls().size();
	    }
	    // TextParseData
	    else if (page.getParseData() instanceof TextParseData)
	    {
	    		TextParseData textParseData = (TextParseData) page.getParseData();
	    		// outgoing links number
	    		outGoingLinks = textParseData.getOutgoingUrls().size();
	    }
	    
	    res.append(outGoingLinks).append(",");
	    Statistics.totalURLExtract.addAndGet(outGoingLinks);
	    
        // content type
        String[] contentType = page.getContentType().split(";");
        res.append(contentType[0]).append("\n");
        Statistics.contentType.put(contentType[0], Statistics.contentType.getOrDefault(contentType[0], 0) + 1);
        
        
	    // cache and clean
        visitCache.add(res.toString());
        if (visitCache.size() >= cacheLimit)
        {
        		CSVFileManager.writeVisitFile(visitCache);
        		visitCache.clear();
        }
	}
}

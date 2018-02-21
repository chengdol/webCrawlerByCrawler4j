import java.util.Arrays;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller
{
	public static void main(String[] args) throws Exception
	{
		String crawlStorageFolder = "/Users/chengdol/eclipse-workspace/csci5272_hw2_revisited/result";
		int numOfCrawler = Runtime.getRuntime().availableProcessors();
		
		// config for storage
		CrawlConfig config = new CrawlConfig();
		// set requirement
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setPolitenessDelay(250);
		// just for testing now
		// need to update
		// 16
		config.setMaxDepthOfCrawling(16);
		// 20000
		config.setMaxPagesToFetch(20000);
		// set binary crawling
		config.setIncludeBinaryContentInCrawling(true);
		
		// initial
		PageFetcher fetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, fetcher);
		CrawlController controller = new CrawlController(config, fetcher, robotstxtServer);
		
		// init files
		CSVFileManager.writeAttemptFile(Arrays.asList("URL,Status-Code,\n"));
		CSVFileManager.writeVisitFile(Arrays.asList("URL,KB,#-Of-Outlinks,Content-Type\n"));
		CSVFileManager.writeAllURLFile(Arrays.asList("URL,Range\n"));
		
		// add seeds
		controller.addSeed("https://www.foxnews.com/");
		controller.addSeed("http://www.foxnews.com/");
		controller.start(MyCrawler.class, numOfCrawler);
		controller.waitUntilFinish();
		
		// close the file
		CSVFileManager.closeFiles();
		Statistics.statWrite();
		System.out.println("close the files");
	}
}

package imdb1000;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;

@SpringBootApplication
public class Crawler {
	final static Logger logger = Logger.getLogger(Crawler.class);
	public static Map<String, Set<String>> movies = new ConcurrentHashMap<String, Set<String>>();
	Set<String> visited = new HashSet<String>();
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	public static AtomicInteger count=new AtomicInteger();

	Set<String> sched = new HashSet<String>();
	public void populate1000(String root) {
		//To prevent dbl scheduling.
        try {
        	
    		ScrapingTask task = new ScrapingTask(root);
        	logger.info("Scheduling task :"+root);
        	executor.execute(task);
        	sched.add(root);
        	
            //2. Fetch the HTML code
            Document document = Jsoup.connect(root).get();
            
            Elements next = document.select("a.lister-page-next");
            for (Element page : next) {
            	String nxt = page.attr("abs:href");
            	if(!sched.contains(nxt)) {
            		populate1000(nxt);
            	} else logger.debug(nxt+" already scheduled... so skipping.");
            }
        } catch (IOException e) {
            logger.error("For '" + root + "': " + e.getMessage());
        }
		
	}
	
	public void map2json()  {
		Gson gson = new Gson(); 
		String json = gson.toJson(movies);
		try (FileWriter file = new FileWriter("movies.json")) {
			file.write(json);
			logger.debug("Successfully Copied JSON Object to File...");
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

		int TOTAL_MOVIES = 49;
		logger.debug("Starting the app...");
		
		Crawler cw = new Crawler();
		String root = "https://www.imdb.com/search/title/?groups=top_1000&sort=user_rating&view=simple";
		Instant start = Instant.now();
		cw.populate1000(root);
		Instant end = Instant.now();
		logger.info("timeelasped: "+Duration.between(end, start).toString());
		
		int retry = 0;
		while(count.get()<TOTAL_MOVIES && retry < 5) {
			try {

				logger.debug("sleeping for some time..."+retry);
				retry++;
				Thread.sleep(TOTAL_MOVIES * 4_000); //@ 4 secs to process a movie
			} catch (InterruptedException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		logger.debug("awakening the app...");
		cw.map2json();
		
		SpringApplication.run(Crawler.class, args);
	}

}

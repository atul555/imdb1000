package imdb1000;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapingTask implements Runnable {
	final static Logger logger = Logger.getLogger(ScrapingTask.class);
    private String name;
	Set<String> visited = new HashSet<String>();
    public ScrapingTask(String name) {
        this.name = name;
    }
 
    public String getName() {
        return name;
    }
	public void populate() {
		
        Set<String> traversed = new HashSet<String>();
        try {
            //1. Fetch the HTML code
            Document document = Jsoup.connect(name).get();
            
            //2. Parse the HTML to extract links to other URLs
            Elements linksOnPage = document.select("a[href^=\"/title/\"]");

            //3. For each extracted URL... go back to Step 4.
            for (Element page : linksOnPage) {
            	String url = page.attr("abs:href");
            	if(!traversed.contains(url)) {
            		populateMovie(url);
            		traversed.add(url);
            	}
            }
        } catch (Exception e) {
            logger.error("For '" + name + "': " + e.getMessage());
        }
		
	}
	public void populateMovie(String URL) {
        if(!visited.contains(URL)) {
            try {
                if (visited.add(URL)) {
                    //System.out.println("populateMovie : "+name+"  ## visited: "+URL);
                }

                Document document = Jsoup.connect(URL).get();

                Elements fullcast = document.select("a[href^=\"fullcredits\"]");
                for (Element page : fullcast) {
                	populateIndex(page.attr("abs:href"));
                	
                }
            } catch (IOException e) {
	            logger.error("For '" + URL + "': " + e.getMessage());
	        }
        }
	}
	public void populateIndex(String URL) {
		String prefix = "https://www.imdb.com/title/";
		
		if(URL.length()<1 || !URL.startsWith(prefix))return;
		
		String id = URL.substring(prefix.length());
		id=id.substring(0, id.indexOf("/"));
		
        if(!visited.contains(id)) {
            try {
                if (visited.add(id)) {
                    //System.out.println("populateIndex: "+name+" :  visited: "+id);
                }

                Document document = Jsoup.connect(URL).get();
                String title = document.title();
                title=title.substring(0, title.indexOf("(")).trim();

                //System.out.println("populateIndex TASK: "+name+"Title: "+Crawler.count.getAndIncrement()+" : "+title);
                
                Set<String> indices = Crawler.movies.get(title);
                if(indices==null) {
                	indices = new HashSet<String>();
                }
                
                Elements e_ind = document.select("td.name");
                for (Element page : e_ind) {
                	String text = page.text().trim();
                	indices.add(text);
//                	System.out.println("\tname: "+text);
                	
                }
                e_ind = document.select("td.character");
                for (Element page : e_ind) {
                	String text = page.text().trim();
                	indices.add(text);
//                	System.out.println("\tcharacter: "+text);
                	
                }
                e_ind = document.select("td a[href^=\"/name/\"]");
                for (Element page : e_ind) {
                	String text = page.text().trim();
                	indices.add(text);
//                	System.out.println("\timg: "+text);
                	
                }
                logger.debug(Crawler.count.getAndIncrement()+" : "+title+" Set size: "+indices.size());
            	Crawler.movies.put(title,  indices);
            } catch (IOException e) {
            	logger.error("For '" + URL + "': " + e.getMessage());
	        }
        }
	}

    public void run() {
		Instant start = Instant.now();

        populate();
        
		Instant end = Instant.now();
		logger.debug("time elasped: "+name+" : "+Duration.between(end, start).toString());

    }
}

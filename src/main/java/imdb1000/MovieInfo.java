package imdb1000;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MovieInfo {

	private String[] query;
	private String[] movies;
	
	public MovieInfo(String[] q) {
		query = q;
	}
	public String[] getQuery() {
		return query;
	}
	public String[] getMovies() {
		Set<String> keys = Crawler.movies.keySet();
		List<String> movys = new ArrayList<String>();
		for(String movie: keys) {
			Set<String> val = Crawler.movies.get(movie);
			boolean contains = true;
			for(String name: query) {
				if(!val.contains(name)) {
					contains=false;
					break;
				}
			}
			if(contains)movys.add(movie);
		}
		movies = movys.toArray(new String[0]); 
		return movies;
	}
}

# IMDB Webscraper 

This is a an app to scrape the top 1000 movies off of IMDb, and collect the mapping of the cast, crew, characters and other names associated with the movie, and make it available for query through REST api.


[follow me on Twitter](https://twitter.com/atul555)


## What is this?

Disclaimer: This project is meant for experimentation and personal use, and not meant to scrap the third party website for professional use.
This is a demo project to show how web crawling/scraping can be achieved in Java. 


## Stack
Java Springboot
jsoup


### Architecture

The application creates a Threadpool of 10 threads and assigns each one a page to IMDB (with a list of 50 movies) to scrape the cast and crew listing off of. It is depth first traversal as each page links to the next set of 50 movies, till all 1000 movies are exhausted.
The movies and the related entries are stored in a map (with 1000 entries). 
The sprinboot exposes a rest api (/movies) which accepts a list of names as the query parameter and returns JSON  of the list of movies, all the  names in the list appear in.  

## Usage

Generate the runnable jar using the maven command.

```
mvn install
```

Run the server using the command.

```
java - jar imdb1000-0.0.1-SNAPSHOT.jar
```

Wait for about 10 minutes for scraping to complete. Monitor the log under logs directory in the main directory to see the progress of movies scraped with the count. Monitor the command line to print Spring splashscreen, which once displayed signifies that server is ready.

Issue the HTTP GET request like below. You can use browser of curl in CLI to issue these:
http://localhost:8080/movies?names=Steven Spielberg
http://localhost:8080/movies?names=Steven Spielberg&names=Tom Hanks
http://localhost:8080/movies?names=Thor&names=Doctor Strange

## License

[MIT](LICENSE)

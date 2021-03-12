package web.simplecrawler.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import web.simplecrawler.web.Interface.LibraryProcessor;
import web.simplecrawler.web.Interface.WebParser;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class WebCrawlerApp {
    public static final int LIMIT = 10;
    private static final Logger logger = LogManager.getLogger(WebCrawlerApp.class);

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please Enter a Search Term: ");
        String searchTerm = scanner.nextLine();

        WebParser parser = new GoogleWebParser(logger);
        LibraryProcessor libraryProcessor = new JavaScriptLibraryProcessor(parser, logger);

        //Library to be processed can be changed later on. In this case it is javascript library processor
        WebCrawler webCrawler = new WebCrawler(searchTerm, parser, libraryProcessor, logger);

        //Crawl Links from Search Engine
        Set<String> links = webCrawler.crawlLinks(LIMIT);

        if(links.size() == 0) {
            System.out.println("Unable to Crawl Links");
            return;
        }

        //Get the top 5 most popular JavaScript Libraries
        List<String> mostPopularJavaScriptLibrary = webCrawler.getMostPopularJavaScriptLibrary(links);

        if (!mostPopularJavaScriptLibrary.isEmpty()) {
            System.out.println("\n\u001B[32mMost Popular Javascript Libaries...");
            mostPopularJavaScriptLibrary.forEach(System.out::println);
        }
    }
}

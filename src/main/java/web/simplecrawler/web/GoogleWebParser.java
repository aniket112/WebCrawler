package web.simplecrawler.web;

import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import web.simplecrawler.web.Interface.WebParser;

import java.io.IOException;
import java.util.Optional;

public class GoogleWebParser implements WebParser {

    private final Logger logger;

    public GoogleWebParser(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Optional<Document> downloadPage(String link) {
        try {
            //without proper User-Agent, we will get 403 error
            Document doc = Jsoup.connect(link).userAgent("Mozilla/5.0").get();
            return Optional.ofNullable(doc);
        } catch (IOException ioException) {
            logger.info("Unable to open downloaded page", ioException);
        } catch (Exception exception){
            logger.error("Exception occurred while co", exception);
        }
        return Optional.empty();
    }

    @Override
    public Elements extractSearchResultsFromPage(Document webDocument){
        //If google search results HTML change the css query "div.kCrYT a"
        //we need to change below accordingly (This info can go in config file)
        return webDocument.select("div.kCrYT a");
    }

    @Override
    public Document parseDocument(String webPage) {
        return Jsoup.parse(webPage);
    }
}

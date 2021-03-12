package web.simplecrawler.web;

import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import web.simplecrawler.web.CustomExceptions.NoSearchResultException;
import web.simplecrawler.web.Interface.LibraryProcessor;
import web.simplecrawler.web.Interface.WebParser;
import web.simplecrawler.web.Utilities.UrlExtractor;

import java.util.*;

public class WebCrawler {
  private final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
  private final String searchTerm;
  private final LibraryProcessor libraryProcessor;
  private final WebParser parser;
  private final Logger logger;

  //Inject Dependencies of library processor and parser
  public WebCrawler(String searchTerm, WebParser parser, LibraryProcessor libraryProcessor, Logger logger) {
    this.searchTerm = searchTerm;
    this.parser = parser;
    this.libraryProcessor = libraryProcessor;
    this.logger = logger;
  }

  /**
   * Crawl Links for the given search term
   * @param limit
   * @return list of links
   */
  public Set<String> crawlLinks(int limit) {
    Set<String> fetchedUrls = new HashSet<String>();

    try {
      String link = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num=" + limit;
      Optional<Document> webDocument = parser.downloadPage(link);

      if (!webDocument.isPresent()) {
        throw new NoSearchResultException("No result from search engine");
      }

      Elements results = parser.extractSearchResultsFromPage(webDocument.get());

      if (results.size() == 0)
        throw new Exception("Unable to parse result");

      for (Element result : results) {
        String linkHref = result.attr("href");
        String url = UrlExtractor.ExtractUrl(linkHref);
        if (!url.isEmpty()) {
          fetchedUrls.add(url);
        }
      }

    } catch (NoSearchResultException noSearchResultException) {
      logger.error("Unable to fetch result from the web ", noSearchResultException);
    } catch (Exception exception) {
      logger.error("Exception occurred",exception);
    }

    return fetchedUrls;
  }

  /**
   * Get the most popular JavaScript Library based on the links crawled
   *
   * @param links
   * @return
   */
  public List<String> getMostPopularJavaScriptLibrary(Set<String> links) {
      return libraryProcessor.getMostPopularLibrary(new ArrayList<>(links));
  }
}


package web.simplecrawler.web.Interface;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Optional;

public interface WebParser {

    /**
     * Download Page from the web
     * @param link
     * @return
     */
    Optional<Document> downloadPage(String link);

    /**
     * Extract Urls from a Web Document
     * @param webDocument
     * @return
     */
    Elements extractSearchResultsFromPage(Document webDocument);

    /**
     * Parse the document using Jsoup Libary
     * @param webPage
     * @return
     */
    Document parseDocument(String webPage);
}

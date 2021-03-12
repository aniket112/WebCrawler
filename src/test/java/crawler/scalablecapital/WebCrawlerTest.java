package crawler.scalablecapital;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import web.simplecrawler.web.GoogleWebParser;
import web.simplecrawler.web.Interface.LibraryProcessor;
import web.simplecrawler.web.Interface.WebParser;
import web.simplecrawler.web.JavaScriptLibraryProcessor;
import web.simplecrawler.web.WebCrawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebCrawlerTest {
    private final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static
    String TEST_DATA_PATH = "src/test/resources/TestData.html";
    private static FileInputStream testhtmldataStream;
    private static String testData;

    @BeforeClass
    public static void setUp() {
        try {
            testhtmldataStream = new FileInputStream(TEST_DATA_PATH);
            testData = IOUtils.toString(testhtmldataStream, StandardCharsets.UTF_8);
        } catch (IOException io) {
            System.out.println("Exception occurred while setting up test data");
        }
    }

    @Test
    public void checkIfLinksCanBeExtracted() {
        Document document = Jsoup.parse(testData);

        WebParser parser = new GoogleWebParser(null);
        Elements element = parser.extractSearchResultsFromPage(document);

        Assert.assertNotNull(element);
        Assert.assertEquals(element.size(), 34);
    }

    @Test
    public void crawlUniqueLinksTest() {
        String searchTerm = "Weather in Celle";
        WebParser parserMock = mock(WebParser.class);
        Elements mockElements = initializeMockElements();

        when(parserMock.downloadPage(anyString())).thenReturn(Optional.of(new Document("https://www.baeldung.com/")));
        when(parserMock.extractSearchResultsFromPage(any())).thenReturn(mockElements);

        WebCrawler crawler = new WebCrawler(searchTerm, parserMock, null, null);
        Set<String> crawledLinks = crawler.crawlLinks(anyInt());

        Assert.assertNotNull(crawledLinks);
        Assert.assertEquals(crawledLinks.size(), 2);
    }

    @Test
    public void extractLibraryNamesByURLTest() {
        Document document = Jsoup.parse(testData);
        WebParser parserMock = mock(WebParser.class);

        when(parserMock.downloadPage(anyString())).thenReturn(Optional.ofNullable(document));
        when(parserMock.parseDocument(anyString())).thenReturn(document);

        LibraryProcessor libraryProcessor = new JavaScriptLibraryProcessor(parserMock, null);
        List<String> result = libraryProcessor.extractLibraryNameByUrl(anyString());

        assertTrue(result.contains("amber.js"));
        assertTrue(result.contains("ionic.js"));
        Assert.assertEquals(result.size(), 19);
    }

    @Test
    public void javaScriptPopularLibraryTest() {
        Document document = Jsoup.parse(testData);
        WebParser parserMock = mock(WebParser.class);
        List<String> expectedResult = Arrays.asList("amber.js", "dojo.js", "babylon.js", "ionic.js", "win.js");

        when(parserMock.downloadPage(anyString())).thenReturn(Optional.ofNullable(document));
        when(parserMock.parseDocument(anyString())).thenReturn(Jsoup.parse(String.valueOf(document)));

        LibraryProcessor libraryProcessor = new JavaScriptLibraryProcessor(parserMock, null);
        List<String> result = libraryProcessor.getMostPopularLibrary(Arrays.asList("www.baeldung.com"));

        Assert.assertEquals(result.size(), 5); //Top 5 popular javascript libraries

        result.forEach(library -> {
            Assert.assertTrue(expectedResult.contains(library));
        });
    }


    @Test
    /**
     * Test to make sure when internet connectivity is available the crawled page always extracts Search Engine results
     */
    public void CrawlerApiTest() {
        WebParser parser = new GoogleWebParser(null);
        String link = GOOGLE_SEARCH_URL + "?q=goa&num=10";

        Optional<Document> webDocument = parser.downloadPage(link);

        if(webDocument.isPresent()) {
            Elements results =  parser.extractSearchResultsFromPage(webDocument.get());
            assertTrue(results.size() > 0);
        } else {
            System.out.println("Could not execute CrawlerApiTest due to network connectivity issue");
        }
    }

    private Elements initializeMockElements() {

        String tag = "<a href=https://de.wikipedia.org/wiki/NASA%23Einrichtungen&amp;sa=U&amp;" +
            "ved=2ahUKEwj6xeb6wqrvAhXOuFkKHdukDYEQ0gIwInoECAUQBQ&amp;usg=AOvVaw0aPeB6ogwQ-0L8SV_TBX0z\"><span " +
            "class=\"XLloXe AP7Wnd\">Einrichtungen</span></a>";

        Attributes attributes1 = new Attributes();
        attributes1.put("href", "https://www.nasa.gov/&amp;sa=U&amp;" +
            "ved=2ahUKEwj6xeb6wqrvAhXOuFkKHdukDYEQFjAAegQIARAB&amp;usg=AOvVaw1_3xcF4svQExClk5f65p5c");

        Attributes attributes2 = new Attributes();
        attributes2.put("href", "https://de.wikipedia.org/wiki/NASA%23Einrichtungen&amp;sa=U&amp");

        List<Element> elements = new ArrayList<>();
        elements.add(new Element(Tag.valueOf(tag), "", attributes1));
        elements.add(new Element(Tag.valueOf(tag), "", attributes1));
        elements.add(new Element(Tag.valueOf(tag), "", attributes2));

        return new Elements(elements);
    }


}

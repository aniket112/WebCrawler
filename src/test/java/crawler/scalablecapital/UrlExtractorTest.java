package crawler.scalablecapital;

import web.simplecrawler.web.Utilities.UrlExtractor;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for Url Extractor Component.
 */
public class UrlExtractorTest
{
    @Test
    public void ValidUrlTest() {
        List<String> validUrls = InitializeValidUrls();

        for (String url : validUrls) {
            String extractedUrl = UrlExtractor.ExtractUrl(url);
            assertFalse(extractedUrl.isEmpty());
        }
    }

    @Test
    public void InvalidValidUrlTest() {
        List<String> inValidUrls = initializeInvalidUrls();

        for (String url : inValidUrls) {
                String extractedUrl = UrlExtractor.ExtractUrl(url);
                System.out.println(extractedUrl);
                assertTrue(extractedUrl.isEmpty());
        }
    }


    private List<String> InitializeValidUrls() {
        return Arrays.asList("http://foo.com/blah_blah", "http://foo.com/blah_blah/", "http://foo.com/blah_blah_" +
                "(wikipedia)", "http://foo.com/blah_blah_(wikipedia)_(again)", "http://www.example.com/wpstyle/?p=364", "https://www.example.com/foo/?bar=baz&inga=42&quux", "ftp://foo.bar/baz", "http://foo.bar/?q=Test%20URL-encoded%20stuff", "http://1337.net", "http://a.b-c.de", "http://223.255.255.254", "http://foo.com/(something)?after=parens", "http://code.google.com/events/#&product=browser", "http://j.mp"
        );
    }

    private List<String> initializeInvalidUrls() {
        return Arrays.asList(
                    "http://", "http://.", "http://..", "http://../", "http://?", "http://??", "http://??/",  "http", "://#", "http://##", "http://##/", "//", "//a", "///a", "///", "http:///a", "foo.com", "rdar://1234", "h://test", "http:// shouldfail.com", ":// should fail", "ftps://foo.bar/", "SMTP://0.0.0.0", "http://3628126748", "http://.www.foo.bar/", "http://.www.foo.bar./"
        );
    }
}

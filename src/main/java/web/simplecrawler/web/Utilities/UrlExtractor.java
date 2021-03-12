package web.simplecrawler.web.Utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlExtractor {
    private static final String URL_PATTERN = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";

    /**
     * Extract Url
     * @param link
     * @return empty string if the url format was invalid
     */
    public static String ExtractUrl(String link) {
        String url = "";
        Pattern urlPattern = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(link);
        try {

            if (!matcher.find()) {
                throw new MalformedURLException("Invalid URL");
            }

            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            String extractedURL = link.substring(matchStart, matchEnd);

            URL urlInstance = new URL(extractedURL);
            url = urlInstance.getProtocol() + "://" + urlInstance.getHost();
        } catch (MalformedURLException urlException) {
            System.out.println("Exception was thrown: \n" + urlException.toString());
        }

        return url;
    }
}

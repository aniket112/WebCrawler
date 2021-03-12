package web.simplecrawler.web.Interface;

import java.util.List;

public interface LibraryProcessor {

    /**
     * Extract Library Name by URL
     * @param url
     * @return
     */
    List<String> extractLibraryNameByUrl(String url);

    /**
     * Get Top 5 Most Popular Library Used
     * @param urls List of Urls
     * @return list of most popular libraries
     */
    List<String> getMostPopularLibrary(List<String> urls);
}

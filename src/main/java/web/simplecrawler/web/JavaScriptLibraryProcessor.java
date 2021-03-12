package web.simplecrawler.web;

import org.apache.logging.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import web.simplecrawler.web.Interface.LibraryProcessor;
import web.simplecrawler.web.Interface.WebParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JavaScriptLibraryProcessor implements LibraryProcessor {

  private final int TIMEOUT = 1;
  private final int N_THREADS = 10;
  private final Logger logger;
  //Multiple threads update this Map concurrently
  private ConcurrentHashMap<String, Integer> javaScriptLibrariesMap = new ConcurrentHashMap<>();
  private WebParser parser;

  /**
   * Inject Dependencies
   *
   * @param parser
   * @param logger
   */
  public JavaScriptLibraryProcessor(WebParser parser, Logger logger) {
    this.parser = parser;
    this.logger = logger;
  }

  /**
   * Extract the names of all the javascript libraries used in the Web Document
   *
   * @param url
   * @return List of javascript library names
   */
  @Override
  public List<String> extractLibraryNameByUrl(String url) {
    List<String> javascriptLibaries = new ArrayList<>();

    try {
      Optional<Document> doc = parser.downloadPage(url);

      if (doc.isPresent()) {
        //Extract List of JavaScript Libraries used by the page
        javascriptLibaries = parser.parseDocument(String.valueOf(doc.get()))
            .select("script")
            .stream()
            .map(element -> element.attr("src"))
            .filter(src -> !StringUtil.isBlank(src) && src.endsWith(".js"))
            .map(r -> r.substring(r.lastIndexOf('/') + 1, r.length()))
            .collect(Collectors.toList());
      }

    } catch (Exception exception) {
      logger.error("Unable to Extract JavaScript Library Names", exception);
    }

    return javascriptLibaries;
  }

  /**
   * Asynchronously for each link
   * 1. Download the page
   * 2. Extract the JavaScript Libraries
   * 3. Store the library names in concurrent map
   *
   * After the thread execution is complete find the top 5 most used libraries
   *
   * @param urls List of Urls
   * @return List of popular javascript libraries
   */
  @Override
  public List<String> getMostPopularLibrary(List<String> urls) {
    List<String> result = new ArrayList<>();

    try {
      ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);

      //Process each Url Concurrently
      for (String url : urls) {
        executorService.execute(() -> {
          ProcessUrl(url);
        });
      }

      executorService.shutdown();
      boolean finished = executorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
      GetMostPopularLibraries(result, finished);

    } catch (InterruptedException interruptedException) {
      logger.error("Thread Execution was interrupted while processing Urls", interruptedException);
    } catch (Exception exception) {
      logger.error("Execption occured while Processing Url in method", exception);
    }
    return result;
  }

  private void ProcessUrl(String url) {
    List<String> libraries = extractLibraryNameByUrl(url);

    if (!libraries.isEmpty()) {
      for (String library : libraries) {
        PutLibraryinMap(library);
      }
    }
  }

  private void PutLibraryinMap(String library) {
    if (javaScriptLibrariesMap.containsKey(library)) {
      int count = javaScriptLibrariesMap.get(library);
      javaScriptLibrariesMap.put(library, ++count);
    } else {
      javaScriptLibrariesMap.put(library, 1);
    }
  }

  private void GetMostPopularLibraries(List<String> result, boolean finished) {
    if (finished) {
      Map<String, Integer> sortedMap = sortByValue(javaScriptLibrariesMap);
      int counter = 5;

      for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
        if (counter == 0) {
          break;
        }
        result.add(entry.getKey());
        counter--;
      }
    }
  }

  private Map<String, Integer> sortByValue(ConcurrentHashMap<String, Integer> popularJavaScriptLibrariesMap) {
    // Create a list from elements of HashMap
    List<Map.Entry<String, Integer>> list =
        new LinkedList<>(popularJavaScriptLibrariesMap.entrySet());

    // Sort the list
    Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

    // put data from sorted list to hashmap
    HashMap<String, Integer> temp = new LinkedHashMap<>();
    for (Map.Entry<String, Integer> aa : list) {
      temp.put(aa.getKey(), aa.getValue());
    }
    return temp;
  }

}

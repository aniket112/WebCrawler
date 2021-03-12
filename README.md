A Simple command-line WebCrawler that counts the most popular Javascript Libraries found on Google.

List of Task covered

0) Read a string (search term) from standard input 
1) Get a Google result page for the search term
2) Extract main result links from the page
3) Download the respective pages and extract the names of Javascript libraries used in them 
4) Print top 5 most used libraries to standard output

Programming Language - Java

Built Using - Maven Compiler 1.7

Libraries Used

jsoup - 1.10.2

junit - 4.11

The solution is comprised of different components
1. WebParser - Responsilble for downloading pages from internet, parsing, extracting search engine result links

2. WebCrawler - It is the main component, responsible for Crawling the links from the Search Engine and finding out the most popular javascript framework based on the search term(It reuses the JavaScriptLibrary Component)

3. JavaScriptLibrary Processor - Includes all the concrete implementation for finding the most popular javascript library

4. Url Extractor Utility - Extract urls from strings 

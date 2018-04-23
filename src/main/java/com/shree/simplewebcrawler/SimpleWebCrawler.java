package com.shree.simplewebcrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Simple WebCrawler Stores data on your local machine on a given depth
 * @author Shreelola Hegde (shrilola.r@gmail.com)
 *
 */
public class SimpleWebCrawler {
    private HashSet<String> links;
    private int currentDepth;
    String url;
    String depth;
    String[] args;
    static final String USAGE_STRING_SHORT =
            "Usage: java [SystemProperties] -jar crawler.jar [-h|-] [<file|folder|url|arg> [<file|folder|url|arg>...]]";

    /**
     * Constrctor
     * @param url url to get crawl data
     * @param depth Depth level
     */
    public SimpleWebCrawler(String url, String depth, String[] args) {
        this.url = url;
        this.depth = depth;
        this.args = args;
        links = new HashSet<String>();
    }

    public  SimpleWebCrawler() {

    }

    /**
     * Create Directory in local storage for saving resources
     * @param path path to create directory
     */
    public void createDirectory(String path) {
        File filepath = new File(path);
        if (!filepath.exists()) {
            if (filepath.mkdirs()) {
                System.out.println("Multiple directories are created!");
            } else {
                System.out.println("Failed to create multiple directories!");
            }
        }
    }

    /**
     * Read the given url and get all links with the given depth
     * @param URL URL to crawl the data
     * @param depth depth to check
     */
    public void getPageLinks(String URL, int depth) {
        currentDepth = 0;
        if ((!links.contains(URL) && (currentDepth < Integer.parseInt(this.depth)))) {
            System.out.println(">> Depth: " + depth + " [" + URL + "]");
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                currentDepth++;
                saveWebPage(URL);
                getStylesAndScripts(URL);
                getAllImages(URL);
                for (Element page : linksOnPage) {
                    String grabUrl = page.attr("abs:href");
                    getPageLinks(grabUrl, depth);
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }

    /**
     * Save page on your system
     * @param page link to save the webpage
     */
    public void saveWebPage(String page) {
        try {
            URL url = new URL(page);
            BufferedReader readr =
                    new BufferedReader(new InputStreamReader(url.openStream()));

            String[] path = ExtractPath(page);
            String path_dir = path[1];
            // Enter filename in which you want to download
            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(path_dir + "/index.html"));

            // read each line from stream till end
            String line;
            while ((line = readr.readLine()) != null) {
                writer.write(line);
            }

            readr.close();
            writer.close();
            System.out.println("Successfully Downloaded.");
        } catch (MalformedURLException mue) {
            System.err.println("Malformed url" + mue.getMessage());

        } catch (IOException ioe) {
            System.err.println("IO Exception "+ ioe.getMessage());
        }

    }

    /**
     * Get All images in the given url
     * It Downloads the image to local directory
     * @param url url to crawl all images
     */
    public void getAllImages(String url) {
        Document doc;
        try {

            //get all images
            doc = Jsoup.connect(url).get();
            Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            for (Element image : images) {
                String srcurl = image.absUrl("src");
                SaveData(srcurl);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Download all js and CSS files
     * @param url  url to crawl all js and image files
     */
    public void getStylesAndScripts(String url) {
        Document doc;
        try {

            //get all images
            doc = Jsoup.connect(url).get();
            Elements csslinks = doc.select("link[href^="+url+"]");
            for (Element link : csslinks) {
                if (link.attr("rel").equals("stylesheet")) {
                    SaveData(link.attr("abs:href"));
                }
            }

            Elements stylelinks = doc.select("script[src]");
            for (Element stylelink: stylelinks) {
                String urlToGrab = stylelink.attr("abs:src");
                if (urlToGrab.startsWith(url)) {
                    System.out.println(urlToGrab);
                    SaveData(urlToGrab);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Extract path to save the data on local
     * @param srcUrl srcurl for getting the name and path of a file
     * @return String array of paths
     */
    private String[] ExtractPath(String srcUrl) {
        String pathInfo[] = new String[2];
        try {
            int indexname = srcUrl.lastIndexOf("/");
            if (indexname == srcUrl.length()) {
                srcUrl = srcUrl.substring(1, indexname);
            }
            indexname = srcUrl.lastIndexOf("/");
            System.out.println(indexname);
            String name = srcUrl.substring(indexname, srcUrl.length());

            int index = 0;
            if (srcUrl.startsWith("http://")) {
                index = 7;
            }
            if (srcUrl.startsWith("https://")) {
                index = 8;
            }

            String path = srcUrl.substring(index, indexname);

            pathInfo[0] = path;
            createDirectory(path);
            pathInfo[1] = name;
        } catch (ArrayIndexOutOfBoundsException aioe) {

        }

        return pathInfo;
    }

    /***
     * Save data on your local
     * @param srcUrl: Source url to save the data
     * @throws IOException
     */
    private void SaveData(String srcUrl) throws  IOException {

        try {
            URL url = new URL(srcUrl);
            String[] pathInfo;
            pathInfo = ExtractPath(srcUrl);

            String path = pathInfo[0];
            String name = pathInfo[1];

            InputStream in = url.openStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream( path + name));
            for (int a; (a = in.read()) != -1;) {
                out.write(a);
            }
            out.close();
            in.close();
        }
        catch (IOException ioe) {
            System.out.println(ioe.getMessage());

        }
    }

    /**
     * Usage details
     */
    private static void usageShort() {
        System.out.println(USAGE_STRING_SHORT+"\n"+
                "       Please invoke with -h option for extended usage help.");
    }

    /**
     * Command line usage
     */

    private static void usage() {
        System.out.println
                (USAGE_STRING_SHORT+"\n\n" +
                        "Supported System Properties and their defaults:\n"+
                        "  -Durl=<url to crawl the data>\n"+
                        "  -Ddepth=<depth to get links>\n"+
                        "This is a simple command line tool for Fetching data from website.\n"+
                        "NOTE: Specifying the url is mandatory.\n" +
                        "Data can be read from files specified as commandline args,\n"+
                        "URLs specified as args, as raw commandline arg strings or via STDIN.\n"+
                        "Examples:\n"+
                        " java -Durl=http://wikipedia.com -Ddepth=2 -jar crawler.jar\n"
                        );
    }

    /**
     * Execute the program
     */
    public void execute() {
            getPageLinks(this.url, currentDepth);
    }

    /**
     * Parsing the arguments
     * @param args args to parse
     * @return SimpleWebcrawler Object
     */
    protected static SimpleWebCrawler parseArgsAndInit(String[] args) {
        String url = System.getProperty("url");
        System.out.println("Shree" + url);
        String depth = System.getProperty("depth");

        if (url == null && depth == null) {
            System.err.println("Specifying url && depth are mandatory.\n" + USAGE_STRING_SHORT);
        }
        System.out.println(args.length);
        return new SimpleWebCrawler(url, depth, args);
    }

    /**
     * Main function
     * @param args
     */
    public static void main(String[] args) {

        if (0 < args.length && ("-help".equals(args[0]) || "--help".equals(args[0]) || "-h".equals(args[0]))) {
            usage();
        } else {
            final SimpleWebCrawler t = parseArgsAndInit(args);
            t.execute();
        }
    }
}

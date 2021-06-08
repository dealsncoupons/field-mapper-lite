package works.hop.field.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crawler {

    Set<String> visitedLinks = new HashSet<>();

    public static void main(String[] args) throws IOException {
        int max = 3;
        Crawler crawler = new Crawler();
        List<String> start = List.of("https://www.cnn.com");
        for (int i = 0; i < max; i++) {
            System.out.println("==>> LEVEL " + i);
            start = crawler.visitWebPage(start);
        }
    }

    public List<String> visitWebPage(List<String> queue) {
        List<String> linksFound = new ArrayList<>();
        for (String link : queue) {
            visitedLinks.add(link);
            System.out.println(link);
            Document document = null;
            try {
                document = Jsoup.connect(link).get();
                Elements elements = document.getElementsByTag("a");
                elements.stream().map(el -> el.attr("href"))
                        .filter(href -> href.startsWith("http"))
                        .forEach(childLink -> {
                            if (!visitedLinks.contains(childLink)) {
                                linksFound.add(childLink);
                            }
                        });
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return linksFound;
    }
}

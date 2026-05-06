package io.muzoo.ssc;

import io.muzoo.ssc.assignment.tracker.SscAssignment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;

public class Main extends SscAssignment {

    public static void main(String[] args) {
        String seed = "https://docs.muzoo.io/";
        Path outputRoot = Paths.get("docs");
        String allowedHost = URI.create(seed).getHost();

        UrlToPathMapper mapper = new UrlToPathMapper(outputRoot);
        FileSaver saver = new FileSaver();
        LinkExtractor extractor = new HtmlLinkExtractor();
        UrlNormalizer normalizer = new UrlNormalizer(allowedHost);
        ProgressReporter reporter = new ConsoleProgressReporter();


        try (HttpDownloader downloader = new HttpDownloader()) {
            WebCrawler crawler = new WebCrawler(downloader, extractor, normalizer, mapper, saver, reporter);
            crawler.crawl(seed);
        } catch (Exception e) {
            throw new RuntimeException("Failed to close HTTP client", e);
        }
    }
}
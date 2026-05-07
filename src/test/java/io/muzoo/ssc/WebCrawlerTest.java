package io.muzoo.ssc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebCrawlerTest {

    @TempDir
    Path tempDir;

    private FakeDownloader downloader;
    private WebCrawler crawler;

    @BeforeEach
    void setUp(){
        downloader = new FakeDownloader();
        LinkExtractor extractor = new HtmlLinkExtractor();
        UrlNormalizer normalizer = new UrlNormalizer("docs.muzoo.io");
        UrlToPathMapper mapper = new UrlToPathMapper(tempDir);
        FileSaver saver = new FileSaver();
        ProgressReporter reporter = new NoOpProgressReporter();

        crawler = new WebCrawler(downloader, extractor, normalizer, mapper, saver, reporter);
    }

    @Test
    void crawlsSinglePageWithNoLinks() throws IOException {
        downloader.respondWithHtml("https://docs.muzoo.io/", "<html><body>hello</body></html>");

        crawler.crawl("https://docs.muzoo.io/");

        assertEquals(List.of("https://docs.muzoo.io/"), downloader.getDownloadLog());
    }

    @Test
    void followsLinksWithinSameDomain() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"foo.html\">Foo</a>");
        downloader.respondWithHtml(
                "https://docs.muzoo.io/foo.html",
                "<html>Foo page</html>");

        crawler.crawl("https://docs.muzoo.io/");

        assertEquals(2, downloader.getDownloadLog().size());
        assertTrue(downloader.getDownloadLog().contains("https://docs.muzoo.io/"));
        assertTrue(downloader.getDownloadLog().contains("https://docs.muzoo.io/foo.html"));
    }

    @Test
    void doesNotFollowOffDomainLinks() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"https://other.com/page.html\">Other</a>");

        crawler.crawl("https://docs.muzoo.io/");

        assertEquals(List.of("https://docs.muzoo.io/"), downloader.getDownloadLog());
    }

    @Test
    void handlesCircularReferencesWithoutInfiniteLoop() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"foo.html\">Foo</a>");
        downloader.respondWithHtml(
                "https://docs.muzoo.io/foo.html",
                "<a href=\"https://docs.muzoo.io/\">Back home</a>");

        crawler.crawl("https://docs.muzoo.io/");

        assertEquals(2, downloader.getDownloadLog().size());
    }

    @Test
    void doesNotRedownloadSameUrl() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"foo.html\">A</a><a href=\"foo.html\">B</a>");
        downloader.respondWithHtml(
                "https://docs.muzoo.io/foo.html",
                "<html></html>");

        crawler.crawl("https://docs.muzoo.io/");

        long fooCount = downloader.getDownloadLog().stream()
                .filter("https://docs.muzoo.io/foo.html"::equals)
                .count();
        assertEquals(1, fooCount);
    }

    @Test
    void skipsUrlOnDownloadException() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"broken.html\">Broken</a><a href=\"good.html\">Good</a>");
        downloader.failOn("https://docs.muzoo.io/broken.html");
        downloader.respondWithHtml("https://docs.muzoo.io/good.html", "ok");

        crawler.crawl("https://docs.muzoo.io/");

        assertTrue(downloader.getDownloadLog().contains("https://docs.muzoo.io/broken.html"));
        assertTrue(downloader.getDownloadLog().contains("https://docs.muzoo.io/good.html"));
    }

    @Test
    void savesNonHtmlContentButDoesNotParseLinks() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<img src=\"image.png\">");
        downloader.respondWithBytes(
                "https://docs.muzoo.io/image.png",
                new byte[]{(byte) 0x89, 'P', 'N', 'G'},
                "image/png");

        crawler.crawl("https://docs.muzoo.io/");

        assertEquals(2, downloader.getDownloadLog().size());
        assertTrue(downloader.getDownloadLog().contains("https://docs.muzoo.io/image.png"));
    }

    @Test
    void rejectsSeedThatNormalizerRejects() {
        assertThrows(IllegalArgumentException.class,
                () -> crawler.crawl("https://fuzzy.com/"));
    }

    @Test
    void crawlWritesContentToDisk() throws IOException {
        downloader.respondWithHtml("https://docs.muzoo.io/", "<html>hi</html>");

        crawler.crawl("https://docs.muzoo.io/");

        Path expected = tempDir.resolve("index.html");
        assertTrue(Files.exists(expected));
        assertEquals("<html>hi</html>", Files.readString(expected));
    }

    @Test
    void resolvesRelativeFragmentsBeforeDeduplication() throws IOException {
        downloader.respondWithHtml(
                "https://docs.muzoo.io/",
                "<a href=\"foo.html#section1\">A</a><a href=\"foo.html#section2\">B</a>");
        downloader.respondWithHtml(
                "https://docs.muzoo.io/foo.html",
                "<html></html>");

        crawler.crawl("https://docs.muzoo.io/");

        long fooCount = downloader.getDownloadLog().stream()
                .filter("https://docs.muzoo.io/foo.html"::equals)
                .count();
        assertEquals(1, fooCount);
    }
}

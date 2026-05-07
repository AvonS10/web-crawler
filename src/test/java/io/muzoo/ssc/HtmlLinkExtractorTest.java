package io.muzoo.ssc;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmlLinkExtractorTest {

    private final LinkExtractor extractor = new HtmlLinkExtractor();

    @Test
    void extractsAnchorHref(){
        Set<String> links = extractor.extractLinks(
                "<html><body><a href=\"foo.html\">Foo</a></body></html>");
        assertEquals(Set.of("foo.html"), links);
    }

    @Test
    void extractsImgSrc() {
        Set<String> links = extractor.extractLinks(
                "<html><body><img src=\"bar.png\"></body></html>");
        assertEquals(Set.of("bar.png"), links);
    }

    @Test
    void extractsLinkHref() {
        Set<String> links = extractor.extractLinks(
                "<html><head><link rel=\"stylesheet\" href=\"style.css\"></head></html>");
        assertEquals(Set.of("style.css"), links);
    }

    @Test
    void extractsScriptSrc() {
        Set<String> links = extractor.extractLinks(
                "<html><body><script src=\"app.js\"></script></body></html>");
        assertEquals(Set.of("app.js"), links);
    }

    @Test
    void extractsIframeSrc() {
        Set<String> links = extractor.extractLinks(
                "<html><body><iframe src=\"frame.html\"></iframe></body></html>");
        assertEquals(Set.of("frame.html"), links);
    }

    @Test
    void deduplicatesUrls() {
        Set<String> links = extractor.extractLinks(
                "<a href=\"foo.html\">A</a><a href=\"foo.html\">B</a>");
        assertEquals(Set.of("foo.html"), links);
    }

    @Test
    void skipsEmptyAttributes() {
        Set<String> links = extractor.extractLinks(
                "<a href=\"\"></a><a href=\"foo.html\"></a>");
        assertEquals(Set.of("foo.html"), links);
    }

    @Test
    void emptyHtmlReturnsEmptySet() {
        Set<String> links = extractor.extractLinks("");
        assertTrue(links.isEmpty());
    }

    @Test
    void htmlWithNoLinksReturnsEmptySet() {
        Set<String> links = extractor.extractLinks(
                "<html><body><p>No links here</p></body></html>");
        assertTrue(links.isEmpty());
    }

    @Test
    void mixedTagsAllExtracted() {
        Set<String> links = extractor.extractLinks(
                "<a href=\"a.html\">A</a>" +
                        "<img src=\"b.png\">" +
                        "<link href=\"c.css\">" +
                        "<script src=\"d.js\"></script>" +
                        "<iframe src=\"e.html\"></iframe>");
        assertEquals(Set.of("a.html", "b.png", "c.css", "d.js", "e.html"), links);
    }

    @Test
    void customTagPairListNarrowsExtraction() {
        LinkExtractor customExtractor = new HtmlLinkExtractor(
                List.of(new HtmlLinkExtractor.TagAttribute("a", "href")));
        Set<String> links = customExtractor.extractLinks(
                "<a href=\"a.html\"></a><img src=\"b.png\">");
        assertEquals(Set.of("a.html"), links);
    }

}

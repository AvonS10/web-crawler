package io.muzoo.ssc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlNormalizerTest {

    private UrlNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new UrlNormalizer("docs.muzoo.io");
    }

    @Test
    @DisplayName("absolute in-domain URL is kept as is")
    void kepsAbsoluteInDomainUrl(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "https://docs.muzoo.io/api/index.html");
        assertEquals(Optional.of("https://docs.muzoo.io/api/index.html"), result
        );
    }

    @Test
    @DisplayName("relative URL with .. is resolved correctly")
    void resolvesRelativeUrlWithParent() {
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/api/",
                "../bar.html");
        assertEquals(Optional.of("https://docs.muzoo.io/bar.html"), result
        );
    }

    @Test
    @DisplayName("abosulte path is resolved against page host")
    void resolvesAbsolutePath(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/api/",
                "/baz.html"
        );
        assertEquals(Optional.of("https://docs.muzoo.io/baz.html"), result);
    }

    @Test
    @DisplayName("fragment is stripped (Hint #3)")
    void stripsFragment(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "foo.html#section");
        assertEquals(Optional.of("https://docs.muzoo.io/foo.html"), result
        );
    }

    @Test
    @DisplayName("query string is stripped (Hint #3)")
    void stripsQueryString(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "foo.html?x=1&y=2"
        );
        assertEquals(Optional.of("https://docs.muzoo.io/foo.html"), result);
    }

    @Test
    @DisplayName("URL-encoded fragment from JDK Javadoc example is stripped")
    void stripsEncodedFragment(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/api/jdk.scripting.nashorn/jdk/nashorn/api/scripting/",
                "URLReader.html#%3Cinit%3E(java.net.URL)"
        );
        assertEquals(Optional.of("https://docs.muzoo.io/api/jdk.scripting.nashorn/jdk/nashorn/api/scripting/URLReader.html"), result);
    }

    @Test
    @DisplayName("off-domain URL is rejected")
    void rejectsOffDomain(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "https://fuzzy.com/foo.html");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("subdomain is rejected (strict host match)")
    void rejectsSubdomain(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "https://api.muzoo.io/foo.html");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("mailto: scheme is rejected")
    void rejectsMailto() {
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "mailto:fuzz@bar.com");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("javascript: scheme is rejected")
    void rejectsJavascript() {
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "javascript:alert('fuzzy')");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("null link returns empty")
    void rejectsNull(){
        Optional<String> result = normalizer.normalize("https://docs.muzoo.io/", null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("blank link returns empty")
    void rejectsBlank() {
        Optional<String> result = normalizer.normalize("https://docs.muzoo.io/", "   ");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("pure fragment (no path) returns empty")
    void rejectsPureFragment(){
        Optional<String> result = normalizer.normalize("https://docs.muzoo.io/", "#furball");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("host comparison is case-insensitive")
    void hostComparisonIsCaseInsensitive(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/",
                "https://Docs.Muzoo.IO/foo.html"
        );
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("relative URL with both fragment and query is normalized")
    void stripsBothFragmentAndQuery(){
        Optional<String> result = normalizer.normalize(
                "https://docs.muzoo.io/api/",
                "foo.html?x=1#bar"
        );
        assertEquals(Optional.of("https://docs.muzoo.io/api/foo.html"), result);
    }
}

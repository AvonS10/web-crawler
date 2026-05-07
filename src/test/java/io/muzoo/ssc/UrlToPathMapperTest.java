package io.muzoo.ssc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UrlToPathMapperTest {

    private UrlToPathMapper mapper;
    private final Path outputRoot = Paths.get("docs");

    @BeforeEach
    void setUp(){
        mapper = new UrlToPathMapper(outputRoot);
    }

    @Test
    void rootUrlMapsToIndexHtml(){
        assertEquals(Paths.get("docs", "index.html"),
                mapper.toPath("https://docs.muzoo.io/"));
    }

    @Test
    void deepFilenameUrlMapsLiterally(){
        assertEquals(Paths.get("docs", "api", "help-doc.html"),
                mapper.toPath("https://docs.muzoo.io/api/help-doc.html"));
    }

    @Test
    void deepDirectoryUrlAppendsIndexHtml(){
        assertEquals(Paths.get("docs", "api", "index.html"),
                mapper.toPath("https://docs.muzoo.io/api/"));
    }

    @Test
    void pngFileUrlMapsLiterally() {
        assertEquals(Paths.get("docs", "api", "java.base", "module-graph.png"),
                mapper.toPath("https://docs.muzoo.io/api/java.base/module-graph.png"));
    }

    @Test
    void noPathTreatedAsRoot() {
        assertEquals(Paths.get("docs", "index.html"),
                mapper.toPath("https://docs.muzoo.io"));
    }

    @Test
    void deeplyNestedFile() {
        assertEquals(
                Paths.get("docs", "api", "jdk.scripting.nashorn", "jdk", "nashorn", "api", "scripting", "URLReader.html"),
                mapper.toPath("https://docs.muzoo.io/api/jdk.scripting.nashorn/jdk/nashorn/api/scripting/URLReader.html"));
    }

}

package io.muzoo.ssc;

import java.net.URI;
import java.nio.file.Path;

public class UrlToPathMapper {
    private final Path outputRoot;

    public UrlToPathMapper(Path outputRoot){
        this.outputRoot = outputRoot;
    }

    public Path toPath(String url){
        URI uri = URI.create(url);
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            path = "/index.html";
        }
        else if (path.endsWith("/")){
            path = path + "index.html";
        }

        String relative = path.startsWith("/") ? path.substring(1) : path;
        return outputRoot.resolve(relative);
    }
}

package io.muzoo.ssc;

import java.util.Set;

public interface LinkExtractor {
    Set<String> extractLinks(String html);
}

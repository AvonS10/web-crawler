package io.muzoo.ssc;

import java.net.URI;
import java.util.Optional;

public class UrlNormalizer {

    private final String allowedHost;

    public UrlNormalizer(String allowedHost){
        this.allowedHost = allowedHost;
    }

    public Optional<String> normalize(String pageUrl, String rawLink){
        if (rawLink == null || rawLink.isBlank()){
            return Optional.empty();
        }

        String stripped = stripFragmentAndQuery(rawLink.trim());
        if (stripped.isEmpty()){
            return Optional.empty();
        }
        URI resolved;
        try{
            resolved = URI.create(pageUrl).resolve(stripped);
        } catch (IllegalArgumentException e){
            return Optional.empty();
        }

        if (!isHttpOrHttps(resolved.getScheme())){
            return Optional.empty();
        }

        if (!allowedHost.equalsIgnoreCase(resolved.getHost())){
            return Optional.empty();
        }
        return Optional.of(resolved.toString());
    }

    private String stripFragmentAndQuery(String url){
        int hashIndex = url.indexOf('#');
        if (hashIndex >= 0){
            url = url.substring(0, hashIndex);
        }

        int queryIndex = url.indexOf('?');
        if (queryIndex >= 0){
            url = url.substring(0, queryIndex);
        }
        return url;
    }

    private boolean isHttpOrHttps(String scheme){
        return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
    }
}

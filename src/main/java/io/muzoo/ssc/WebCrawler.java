package io.muzoo.ssc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class WebCrawler {

    private final Downloader downloader;
    private final LinkExtractor linkExtractor;
    private final UrlNormalizer urlNormalizer;
    private final UrlToPathMapper pathMapper;
    private final FileSaver fileSaver;

    public WebCrawler(Downloader downloader, LinkExtractor linkExtractor, UrlNormalizer urlNormalizer, UrlToPathMapper pathMapper, FileSaver fileSaver){
        this.downloader = downloader;
        this.linkExtractor = linkExtractor;
        this.urlNormalizer = urlNormalizer;
        this.pathMapper = pathMapper;
        this.fileSaver = fileSaver;
    }

    public void crawl(String seed){
        Optional<String> normalizedSeed = urlNormalizer.normalize(seed, seed);
        if (normalizedSeed.isEmpty()){
            throw new IllegalArgumentException("Seed URL was rejected by normalizer: " + seed);
        }

        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        queue.add(normalizedSeed.get());
        visited.add(normalizedSeed.get());

        while (!queue.isEmpty()){
            String currentUrl = queue.poll();
            try{
                Set<String> discovered = processUrl(currentUrl);
                for (String link : discovered){
                    if (visited.add(link)) {
                        queue.add(link);
                    }
                }
            } catch (IOException e){
                System.err.println("Failed to process " + currentUrl + ": " + e.getMessage());
            }
        }
    }


    private Set<String> processUrl(String currentUrl) throws IOException {
        DownloadResult result = downloader.download(currentUrl);
        Path diskPath = pathMapper.toPath(currentUrl);
        fileSaver.save(diskPath, result.body());
        System.out.println("Downloaded " + currentUrl + " -> " + diskPath);

        if (!result.isHtml()){
            return Set.of();
        }

        String html = new String(result.body(), StandardCharsets.UTF_8);
        Set<String> rawLinks = linkExtractor.extractLinks(html);
        Set<String> normalizedLinks = new LinkedHashSet<>();
        for (String rawLink : rawLinks){
            urlNormalizer.normalize(currentUrl, rawLink).ifPresent(normalizedLinks::add);
        }
        return  normalizedLinks;
    }
}

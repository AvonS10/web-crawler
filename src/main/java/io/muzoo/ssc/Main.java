package io.muzoo.ssc;

import io.muzoo.ssc.assignment.tracker.SscAssignment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Main extends SscAssignment {

    public static void main(String[] args) {
        String seed = "https://docs.muzoo.io/";
        Path outputRoot = Paths.get("docs");
        UrlToPathMapper mapper = new UrlToPathMapper(outputRoot);
        FileSaver saver = new FileSaver();
        LinkExtractor extractor = new HtmlLinkExtractor();


        try (HttpDownloader downloader = new HttpDownloader()) {
            DownloadResult result = downloader.download(seed);
            Path diskPath = mapper.toPath(seed);
            saver.save(diskPath, result.body());
            System.out.println("Saved " + result.body().length + " bytes to " + diskPath);

            if (result.isHtml()){
                String html = new String(result.body(), StandardCharsets.UTF_8);
                Set<String> links = extractor.extractLinks(html);
                System.out.println("Found " + links.size() + " link(s) in " + seed + ":");
                links.forEach(link -> System.out.println("  " + link));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process " + seed, e);
        }
    }
}
package io.muzoo.ssc;

import io.muzoo.ssc.assignment.tracker.SscAssignment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends SscAssignment {

    public static void main(String[] args) {
        String seed = "https://docs.muzoo.io/";
        Path outputRoot = Paths.get("docs");
        UrlToPathMapper mapper = new UrlToPathMapper(outputRoot);
        FileSaver saver = new FileSaver();


        try (HttpDownloader downloader = new HttpDownloader()) {
            DownloadResult result = downloader.download(seed);
            Path diskPath = mapper.toPath(seed);
            saver.save(diskPath, result.body());
            System.out.println("Saved " + result.body().length + " bytes to " + diskPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process " + seed, e);
        }
    }
}
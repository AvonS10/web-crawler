package io.muzoo.ssc;

import io.muzoo.ssc.assignment.tracker.SscAssignment;
import java.io.IOException;

public class Main extends SscAssignment {

    public static void main(String[] args) {
        String seed = "https://docs.muzoo.io/";
        try (HttpDownloader downloader = new HttpDownloader()) {
            DownloadResult result = downloader.download(seed);
            System.out.println("Downloaded " + result.body().length + " bytes from " + seed);
            System.out.println("Content-Type: " + result.contentType());
        } catch (Exception e) {
            throw new RuntimeException("Failed to download " + seed, e);
        }
    }
}
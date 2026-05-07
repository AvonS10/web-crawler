package io.muzoo.ssc;

public interface Downloader {
    DownloadResult download(String url) throws DownloadException;
}


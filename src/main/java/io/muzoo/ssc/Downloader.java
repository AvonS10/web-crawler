package io.muzoo.ssc;

import java.io.IOException;

public interface Downloader {
    DownloadResult download(String url) throws IOException;
}


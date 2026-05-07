package io.muzoo.ssc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class FakeDownloader implements Downloader {

    private final Map<String, DownloadResult> responses = new HashMap<>();
    private final Set<String> failingUrls = new HashSet<>();
    private final List<String> downloadLog = new ArrayList<>();

    void respondWithHtml(String url, String html){
        responses.put(url, new DownloadResult(
                html.getBytes(StandardCharsets.UTF_8),
                "text/html; charset=UTF-8"
        ));
    }

    void respondWithBytes(String url, byte[] body, String contentType){
        responses.put(url, new DownloadResult(body, contentType));
    }

    void failOn(String url){
        failingUrls.add(url);
    }

    @Override
    public DownloadResult download(String url) throws DownloadException{
        downloadLog.add(url);
        if (failingUrls.contains(url)){
            throw new DownloadException(url, "fake failure");
        }
        DownloadResult response = responses.get(url);
        if (response == null){
            throw new DownloadException(url, "no fake response configured for " + url);
        }
        return response;
    }

    List<String> getDownloadLog(){
        return downloadLog;
    }
}

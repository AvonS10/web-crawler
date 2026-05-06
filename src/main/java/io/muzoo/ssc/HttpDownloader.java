package io.muzoo.ssc;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class HttpDownloader implements Downloader, AutoCloseable {

    private final CloseableHttpClient client;

    public HttpDownloader() {
        this.client = HttpClients.createDefault();
    }

    @Override
    public DownloadResult download(String url) throws IOException{
        HttpGet request = new HttpGet(url);
        return client.execute(request, response -> {
            HttpEntity entity = response.getEntity();
            byte[] body = (entity != null) ? EntityUtils.toByteArray(entity) : new byte[0];
            String contentType = (entity != null) ? entity.getContentType() : null;
            return new DownloadResult(body, contentType);
        });
    }

    @Override
    public void close() throws IOException{
        client.close();
    }
}

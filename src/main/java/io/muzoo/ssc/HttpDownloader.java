package io.muzoo.ssc;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

public class HttpDownloader implements Downloader, AutoCloseable {

    private static final Timeout CONNECT_TIMEOUT = Timeout.ofSeconds(10);
    private static final Timeout RESPONSE_TIMEOUT = Timeout.ofSeconds(30);

    private final CloseableHttpClient client;

    public HttpDownloader() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setResponseTimeout(RESPONSE_TIMEOUT)
                .build();

        this.client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public DownloadResult download(String url) throws DownloadException{
        HttpGet request = new HttpGet(url);
        try {
            return client.execute(request, response -> {
                int status = response.getCode();
                if (status < 200 || status >= 300) {
                    throw new DownloadException(url, "HTTP " + status);
                }

                HttpEntity entity = response.getEntity();
                byte[] body = (entity != null) ? EntityUtils.toByteArray(entity) : new byte[0];
                String contentType = (entity != null) ? entity.getContentType() : null;
                return new DownloadResult(body, contentType);
            });
        } catch (DownloadException e){
            throw e;
        } catch (IOException e){
            throw new DownloadException(url, "Network error: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException{
        client.close();
    }
}

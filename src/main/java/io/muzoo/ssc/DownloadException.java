package io.muzoo.ssc;

import java.io.IOException;

public class DownloadException extends IOException{

    private final String url;

    public DownloadException(String url, String message){
        super(message);
        this.url = url;
    }

    public DownloadException(String url, String message, Throwable cause){
        super(message, cause);
        this.url = url;
    }

    public String getUrl(){
        return url;
    }
}

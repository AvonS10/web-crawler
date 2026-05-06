package io.muzoo.ssc;

public record DownloadResult(byte[] body, String contentType) {

    public boolean isHtml(){
        return contentType != null && contentType.toLowerCase().startsWith("text/html");
    }
}

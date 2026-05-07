package io.muzoo.ssc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DownloadResultTest {

    @Test
    void isHtmlForTextHtml() {
        DownloadResult result = new DownloadResult(new byte[0], "text/html");
        assertTrue(result.isHtml());
    }

    @Test
    void isHtmlForTextHtmlWithCharset() {
        DownloadResult result = new DownloadResult(new byte[0], "text/html; charset=UTF-8");
        assertTrue(result.isHtml());
    }

    @Test
    void isHtmlIsCaseInsensitive() {
        DownloadResult result = new DownloadResult(new byte[0], "TEXT/HTML");
        assertTrue(result.isHtml());
    }

    @Test
    void notHtmlForImage() {
        DownloadResult result = new DownloadResult(new byte[0], "image/png");
        assertFalse(result.isHtml());
    }

    @Test
    void notHtmlForNullContentType() {
        DownloadResult result = new DownloadResult(new byte[0], null);
        assertFalse(result.isHtml());
    }

    @Test
    void notHtmlForCss() {
        DownloadResult result = new DownloadResult(new byte[0], "text/css");
        assertFalse(result.isHtml());
    }

}

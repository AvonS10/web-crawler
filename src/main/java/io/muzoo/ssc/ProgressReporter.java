package io.muzoo.ssc;

public interface ProgressReporter {
    void report(int downloaded, int total, String currentUrl);
}

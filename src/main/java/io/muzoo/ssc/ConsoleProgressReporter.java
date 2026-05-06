package io.muzoo.ssc;

import java.util.Locale;

public class ConsoleProgressReporter implements  ProgressReporter {

    @Override
    public void report(int downloaded, int total, String currentUrl){
        double percentage = (total == 0) ? 0.0 : (downloaded * 100.0 / total);
        System.out.printf(
                Locale.US,
                "> %.1f%% (%,d/%,d urls are downloaded) - %s%n",
                percentage, downloaded, total, currentUrl
        );
    }
}

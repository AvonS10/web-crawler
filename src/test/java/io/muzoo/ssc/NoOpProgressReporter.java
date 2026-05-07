package io.muzoo.ssc;

class NoOpProgressReporter implements ProgressReporter{

    @Override
    public void report(int downloaded, int total, String currentUrl){
        //empty by intention so progress bar don't clutter test results
    }
}

package io.muzoo.ssc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleProgressReporterTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    @BeforeEach
    void redirectStdout(){
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    void restoreStdout(){
        System.setOut(originalOut);
    }

    @Test
    void formatsExactAssignmentExample() {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        reporter.report(1023, 20004, "https://docs.muzoo.io/foo");

        assertEquals(
                "> 5.1% (1,023/20,004 urls are downloaded) - https://docs.muzoo.io/foo"
                        + System.lineSeparator(),
                captured.toString());
    }

    @Test
    void zeroTotalAvoidsDivisionByZero() {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        reporter.report(0, 0, "https://docs.muzoo.io/");
        assertEquals(
                "> 0.0% (0/0 urls are downloaded) - https://docs.muzoo.io/"
                        + System.lineSeparator(),
                captured.toString());
    }

    @Test
    void simpleHalfwayCase() {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        reporter.report(1, 2, "https://docs.muzoo.io/");

        assertEquals(
                "> 50.0% (1/2 urls are downloaded) - https://docs.muzoo.io/"
                        + System.lineSeparator(),
                captured.toString());
    }

    @Test
    void usesCommaThousandsSeparatorRegardlessOfLocale() {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        reporter.report(1234567, 9999999, "https://docs.muzoo.io/");

        assertTrue(captured.toString().contains("1,234,567/9,999,999"));
    }

    private static void assertTrue(boolean condition) {
        org.junit.jupiter.api.Assertions.assertTrue(condition);
    }
}

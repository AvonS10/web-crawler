package io.muzoo.ssc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSaverTest {

    private final FileSaver saver = new FileSaver();

    @Test
    void writesBytesToFile(@TempDir Path tempDir) throws IOException{
        Path target = tempDir.resolve("out.html");
        byte[] content = "<html>haii</html>".getBytes();

        saver.save(target, content);

        assertArrayEquals(content, Files.readAllBytes(target));
    }

    @Test
    void createsParentDirectories(@TempDir Path tempDir) throws IOException{
        Path target = tempDir.resolve("api").resolve("deep").resolve("file.html");
        byte[] content = "data".getBytes();

        saver.save(target, content);

        assertTrue(Files.exists(target));
        assertArrayEquals(content, Files.readAllBytes(target));
    }

    @Test
    void overwritesExistingFile(@TempDir Path tempDir) throws IOException{
        Path target = tempDir.resolve("out.html");
        saver.save(target, "first".getBytes());
        saver.save(target, "second".getBytes());

        assertArrayEquals("second".getBytes(), Files.readAllBytes(target));
    }
}

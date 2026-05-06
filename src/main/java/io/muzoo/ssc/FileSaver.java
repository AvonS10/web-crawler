package io.muzoo.ssc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSaver {

    public void save(Path destination, byte[] content) throws IOException{
        Path parent = destination.getParent();
        if  (parent != null){
            Files.createDirectories(parent); //will create missing directories. Will do nothing if a directory already exists (wont overwrite).
        }
        Files.write(destination, content);
    }
}

package com.fileevent.fileapi.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    private Path directory = Path.of("./data/files");

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(Path directory) {
        this.directory = directory;
    }

}

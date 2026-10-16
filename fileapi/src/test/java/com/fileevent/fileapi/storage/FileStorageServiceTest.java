package com.fileevent.fileapi.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldStoreFileAndCalculateSha256() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDirectory(temporaryDirectory);

        FileStorageService service = new FileStorageService(properties);

        byte[] content = "conteudo de teste"
                .getBytes(StandardCharsets.UTF_8);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.txt",
                "text/plain",
                content
        );

        StoredObject storedObject = service.store(file);

        String expectedSha256 = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(content)
        );

        assertThat(storedObject.sizeBytes()).isEqualTo(content.length);
        assertThat(storedObject.sha256()).isEqualTo(expectedSha256);

        Path storedPath = temporaryDirectory.resolve(
                storedObject.storageKey()
        );

        assertThat(Files.exists(storedPath)).isTrue();

        try (InputStream input = service.open(storedObject.storageKey())) {
            assertThat(input.readAllBytes()).isEqualTo(content);
        }
    }

    @Test
    void shouldRejectEmptyFile() {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDirectory(temporaryDirectory);

        FileStorageService service = new FileStorageService(properties);

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        assertThatThrownBy(() -> service.store(emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O arquivo não pode estar vazio.");
    }

    @Test
    void shouldDeleteStoredFile() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.setDirectory(temporaryDirectory);

        FileStorageService service = new FileStorageService(properties);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.txt",
                "text/plain",
                "conteudo".getBytes(StandardCharsets.UTF_8)
        );

        StoredObject storedObject = service.store(file);
        Path storedPath = temporaryDirectory.resolve(
                storedObject.storageKey()
        );

        service.delete(storedObject.storageKey());

        assertThat(Files.exists(storedPath)).isFalse();
    }
}
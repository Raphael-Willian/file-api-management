package com.fileevent.fileapi.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDirectory;

    public FileStorageService(FileStorageProperties properties) {
        this.storageDirectory = properties
                .getDirectory()
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Não foi possível inicializar o diretório de armazenamento.",
                    exception
            );
        }
    }

    public StoredObject store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "O arquivo não pode estar vazio."
            );
        }

        MessageDigest digest = createSha256();
        Path temporaryFile = null;

        try {
            temporaryFile = Files.createTempFile(
                    storageDirectory,
                    ".upload-",
                    ".tmp"
            );

            try (
                    InputStream input = file.getInputStream();
                    DigestInputStream digestInput =
                            new DigestInputStream(input, digest)
            ) {
                Files.copy(
                        digestInput,
                        temporaryFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            long sizeBytes = Files.size(temporaryFile);

            if (sizeBytes == 0) {
                throw new IllegalArgumentException(
                        "O arquivo não pode estar vazio."
                );
            }

            String storageKey = UUID.randomUUID().toString();
            Path destination = resolvePath(storageKey);

            try {
                Files.move(
                        temporaryFile,
                        destination,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, destination);
            }

            temporaryFile = null;

            String sha256 = HexFormat.of()
                    .formatHex(digest.digest());

            return new StoredObject(
                    storageKey,
                    sizeBytes,
                    sha256
            );

        } catch (IllegalArgumentException exception) {
            cleanupTemporaryFile(temporaryFile, exception);
            throw exception;

        } catch (IOException exception) {
            cleanupTemporaryFile(temporaryFile, exception);

            throw new FileStorageException(
                    "Não foi possível armazenar o arquivo.",
                    exception
            );
        }
    }

    public InputStream open(String storageKey) {
        Path path = resolvePath(storageKey);

        try {
            if (!Files.isRegularFile(path)) {
                throw new FileStorageException(
                        "O arquivo não foi encontrado no armazenamento."
                );
            }

            return Files.newInputStream(path);

        } catch (IOException exception) {
            throw new FileStorageException(
                    "Não foi possível abrir o arquivo.",
                    exception
            );
        }
    }

    public void delete(String storageKey) {
        Path path = resolvePath(storageKey);

        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new FileStorageException(
                    "Não foi possível remover o arquivo do armazenamento.",
                    exception
            );
        }
    }

    private Path resolvePath(String storageKey) {
        if (storageKey == null) {
            throw new IllegalArgumentException(
                    "A chave de armazenamento não pode ser nula."
            );
        }

        UUID fileId;

        try {
            fileId = UUID.fromString(storageKey);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Chave de armazenamento inválida.",
                    exception
            );
        }

        Path path = storageDirectory
                .resolve(fileId.toString())
                .normalize();

        if (!path.startsWith(storageDirectory)) {
            throw new IllegalArgumentException(
                    "A chave aponta para um caminho inválido."
            );
        }

        return path;
    }

    private MessageDigest createSha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "O algoritmo SHA-256 não está disponível.",
                    exception
            );
        }
    }

    private void cleanupTemporaryFile(
            Path temporaryFile,
            Exception originalException
    ) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }
}
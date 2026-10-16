package com.fileevent.fileapi.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository //Representação semântica, não funcional...
public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {
}

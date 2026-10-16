package com.fileevent.fileapi.delivery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository //Novamente, representação semântica...
public interface FileDeliveryRepository extends JpaRepository<FileDelivery, UUID> {

    //Na entidade foi determinado o FetchType como Lazy. Essa é a consulta quandoo precisarmos desses dads file_id
    List<FileDelivery> findAllByFile_IdOrderByCreatedAtDesc(UUID fileId);

}

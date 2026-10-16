package com.fileevent.fileapi.delivery;

import com.fileevent.fileapi.file.StoredFile;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "file_delivery")
public class FileDelivery {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID idFileDelivery;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false) // Talvez seja considerável: updatable = false
    private StoredFile idStoredFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private DeliveryChannel channel;

    @Column(name = "recipient", length = 320)
    private String recipient;

    @Column(length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FileDelivery() {}

    private FileDelivery(StoredFile idStoredFile, DeliveryChannel channel, String recipient, String subject, String message) {
        this.idFileDelivery = UUID.randomUUID();
        this.idStoredFile = idStoredFile;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message == null ? "" : message;
        this.status = DeliveryStatus.PENDING;
        this.attemptCount = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public static FileDelivery create(StoredFile file, DeliveryChannel channel, String recipient, String subject, String message) {
        return new FileDelivery(file, channel, recipient, subject, message);
    }

    public void markProcessing() {
        this.status = DeliveryStatus.PROCESSING;
        this.attemptCount ++;
        this.lastError = null;
        this.updatedAt = Instant.now();
    }


    public void markSent() {
        this.status = DeliveryStatus.SENT;
        this.lastError = null;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.status = DeliveryStatus.FAILED;
        this.lastError = error;
        this.updatedAt = Instant.now();
    }


    public UUID getIdFileDelivery() {
        return idFileDelivery;
    }

    public StoredFile getIdStoredFile() {
        return idStoredFile;
    }

    public DeliveryChannel getChannel() {
        return channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

}

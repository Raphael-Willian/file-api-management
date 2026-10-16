package com.fileevent.fileapi.storage;

public record StoredObject(String storageKey, long sizeBytes, String sha256) {
}

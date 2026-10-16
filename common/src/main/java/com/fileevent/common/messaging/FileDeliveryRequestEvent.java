package com.fileevent.common.messaging;

import java.util.Objects;
import java.util.UUID;

public record FileDeliveryRequestEvent(UUID deliveryId) {

    public FileDeliveryRequestEvent{
        Objects.requireNonNull(deliveryId, "O ID do delivery não pode ser nulo.");
    }

}

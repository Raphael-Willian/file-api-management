package com.fileevent.common.messaging;

/**
 * O objetivo dessa classe é centralizar o contrato de mensageria que será utilizado por todos os demais projetos.
 */

public final class FileMessagingContract {

    //EVENTS EXCHANGE, ROUTING KEY DE DELIVERY, QUEUE DELIVERY, DEAD LETTER EXCHANGE (DLX), DEAD LETTER RK, DEAD LETTER QUEUE

    public static final String EXENTS_EXCHANGE = "file.events.exchange";

    public static final String RK_DELIVERY = "file.delivery.requested";
    public static final String DELIVERY_QUEUE = "file.delivery.queue";

    public static final String EVENT_DLX = "file.events.dlx";
    public static final String DEAD_LETTER_ROUTING_KEY  = "file.delivery.dead";

    public static final String DEAD_LETTER_QUEUE = "file.delivery.dlq";

    private FileMessagingContract() {

    }

}

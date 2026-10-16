package com.fileevent.common.messaging;

/**
 * O objetivo dessa classe é centralizar o contrato de mensageria que será utilizado por todos os demais projetos.
 */

public final class FileMessagingContract {

    /**
     * Fluxo para facilitar entendimento:
     *
     * Se der certo: Evento -> Exchange -> Routing Key -> QueueExample
     * Se der errado: QueueExample -> Exchange Dead Letter (DLX) -> Routing Key (Agora é a da DLQ) -> Queue Dead Letter
     *
     * Porque isso acaba sendo importante? As mensagens que não foram processadas por algum motivo, precisam ser
     * reprocessadas em determinaos cenários. Então o fluxo é refeito, porém, não utilizando a mesma fila topológica.
     * Agora o processamento parte da fila topológica da DLX.
     *
     * OBS: Exchange "Se conecta" a queue de destino "utilizando uma routing key". Ao menos na modalidade Direct e Topic;
     */

    public static final String EVENTS_EXCHANGE = "file.events.exchange";
    public static final String EVENT_DLX = "file.events.dlx";

    public static final String DEAD_LETTER_QUEUE = "file.delivery.dlq";
    public static final String DELIVERY_QUEUE = "file.delivery.queue";

    public static final String RK_DELIVERY = "file.delivery.requested";
    public static final String DEAD_LETTER_ROUTING_KEY  = "file.delivery.dead";

    private FileMessagingContract() {

    }

}

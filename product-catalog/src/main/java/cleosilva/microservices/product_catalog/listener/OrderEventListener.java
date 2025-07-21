package cleosilva.microservices.product_catalog.listener;

import cleosilva.microservices.product_catalog.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveOrderCreatedEvent(String orderDetails) {
        System.out.println("--------------------------------------------------");
        System.out.println("Microsserviço Product Catalog recebeu evento de pedido:");
        System.out.println("Detalhes do Pedido: " + orderDetails);
        // Aqui você adicionaria a lógica de negócio real, como:
        // - Parsar o JSON 'orderDetails' para um objeto de Pedido
        // - Atualizar o estoque dos produtos envolvidos
        // - Logar o evento em um banco de dados de auditoria
        System.out.println("--------------------------------------------------");
    }
}

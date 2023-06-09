package br.com.bios.aws_project02.service;

import br.com.bios.aws_project02.models.Envelope;
import br.com.bios.aws_project02.models.ProductEvent;
import br.com.bios.aws_project02.models.ProductEventLog;
import br.com.bios.aws_project02.models.SnsMessage;
import br.com.bios.aws_project02.repository.ProductEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventsConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsConsumer.class);

    private ObjectMapper objectMapper;
    private ProductEventLogRepository productEventLogRepository;

    @Autowired
    private ProductEventsConsumer(ObjectMapper objectMapper, ProductEventLogRepository productEventLogRepository){
        this.objectMapper = objectMapper;
        this.productEventLogRepository = productEventLogRepository;
    }

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage message) throws JMSException, IOException {
        SnsMessage snsMessage = objectMapper.readValue(message.getText(), SnsMessage.class);

        Envelope envelope = objectMapper.readValue(snsMessage.getMessage(), Envelope.class);

        ProductEvent productEvent = objectMapper.readValue(envelope.getData(), ProductEvent.class);
        LOGGER.info("Product event received - Event: {} - ProductId: {} - MessageId: {}",
                envelope.getEventType(),
                productEvent.getProductId(),
                snsMessage.getMessageId());

        ProductEventLog productEventLog = buildProductEventLog(envelope, productEvent, snsMessage);
        productEventLogRepository.save(productEventLog);
    }

    private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent, SnsMessage snsMessage) {
        long timestamp = Instant.now().toEpochMilli();
        ProductEventLog productEventLog = new ProductEventLog();
        productEventLog.setPk(productEvent.getCode());
        productEventLog.setSk(envelope.getEventType() + "_" + timestamp);
        productEventLog.setEventType(envelope.getEventType());
        productEventLog.setProductId(productEvent.getProductId());
        productEventLog.setUsername(productEvent.getUsername());
        productEventLog.setTimestamp(timestamp);
        productEventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond());
        productEventLog.setMessageId(snsMessage.getMessageId());
        return productEventLog;
    }
}

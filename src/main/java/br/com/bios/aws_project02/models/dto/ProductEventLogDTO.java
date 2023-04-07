package br.com.bios.aws_project02.models.dto;

import br.com.bios.aws_project02.enums.EventType;
import br.com.bios.aws_project02.models.ProductEventLog;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;

public class ProductEventLogDTO {

    private String code;
    private EventType eventType;
    private long productId;
    private String username;
    private long timestamp;

    public ProductEventLogDTO(ProductEventLog productEventLog) {
        this.code = productEventLog.getPk();
        this.eventType = productEventLog.getEventType();
        this.productId = productEventLog.getProductId();
        this.username = productEventLog.getUsername();
        this.timestamp = productEventLog.getTimestamp();
    }

    public String getCode() {
        return code;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getProductId() {
        return productId;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

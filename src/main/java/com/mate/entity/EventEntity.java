package com.mate.entity;

import com.mate.entity.enums.EventSize;
import com.mate.entity.enums.EventStatus;
import com.mate.entity.enums.EventType;
import com.mate.model.RefundPolicy;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event")
public class EventEntity {
    @Id
    private String eventId;
    private String name;
    private EventSize eventSize;
    private EventType eventType;
    private EventStatus eventStatus;
    private Double joiningFee;
    private RefundPolicy refundPolicy;
    private Integer capacity;

    private String hostId;
    private Point location;
    private List<String> attendees;

    @Transient
    public static final String SEQUENCE_NAME = "event_sequence";

}

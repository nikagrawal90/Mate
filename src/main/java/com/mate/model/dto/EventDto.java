package com.mate.model.dto;

import com.mate.entity.UserEntity;
import com.mate.entity.enums.EventSize;
import com.mate.entity.enums.EventStatus;
import com.mate.entity.enums.EventType;
import com.mate.model.RefundPolicy;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.util.List;

@Data
public class EventDto {
    private String eventId;
    private String eventName;
    private EventSize eventSize;
    private EventType eventType;
    private EventStatus eventStatus;
    private String hostId;
    private Point location;
    private Double joiningFee;
    private Integer capacity;
    private RefundPolicy refundPolicy;
    private List<UserEntity> attendees;
}

package com.mate.service;

import com.mate.entity.EventEntity;
import com.mate.entity.enums.EventStatus;
import com.mate.exception.*;
import com.mate.model.dto.EventDto;
import com.mate.model.dto.request.UpdateEventRequest;
import com.mate.repository.EventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public String createEvent(EventDto eventDto) {
        EventEntity eventEntity = new EventEntity();
        BeanUtils.copyProperties(eventDto, eventEntity);

        eventRepository.save(eventEntity);

        return eventEntity.getEventId();
    }

    public EventEntity getEventById(String eventId) throws EventNotFoundException {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event=%s not found", eventId)));
    }

    public void deleteEvent(String eventId) throws EventNotFoundException {
        if(eventRepository.findById(eventId).isPresent()) {
            cancelEvent(eventId);
            EventEntity eventEntity = getEventById(eventId);
            eventEntity.setEventStatus(EventStatus.DELETED);
            eventRepository.save(eventEntity);
        } else {
            throw new EventNotFoundException(String.format("Event=%s not found", eventId));
        }
    }

    public List<EventDto> getAllEvents() {
        List<EventEntity> eventEntities = eventRepository.findAll();
        List<EventDto> eventDtoList = new ArrayList<>();

        eventEntities.forEach(eventEntity -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(eventEntity, eventDto);
            eventDtoList.add(eventDto);
        });

        return eventDtoList;
    }

    public void cancelEvent(String eventId) throws EventNotFoundException {
        Optional<EventEntity> eventEntityOptional = eventRepository.findById(eventId);
        if(eventEntityOptional.isPresent()) {
            EventEntity eventEntity = eventEntityOptional.get();
            eventEntity.setEventStatus(EventStatus.CANCELLED);
            eventRepository.save(eventEntity);
        } else {
            throw new EventNotFoundException(String.format("Event=%s not found", eventId));
        }
    }

    public EventDto updateEvent(UpdateEventRequest updateEventRequest) throws UnauthorizedException, EventNotFoundException {
        Optional<EventEntity> oldEventEntityOptional = eventRepository.findById(updateEventRequest.getEventId());

        if(oldEventEntityOptional.isPresent()){
            EventEntity oldEventEntity = oldEventEntityOptional.get();
            if(oldEventEntity.getHostId().equals(updateEventRequest.getRequesterId())) {
                oldEventEntity.setEventStatus(updateEventRequest.getEventStatus());
                oldEventEntity.setLocation(updateEventRequest.getLocation());
                oldEventEntity.setName(updateEventRequest.getName());
                oldEventEntity.setCapacity(updateEventRequest.getCapacity());
                eventRepository.save(oldEventEntity);

                String responseMessage = "Updated event with id=" + updateEventRequest.getEventId();

                log.info(responseMessage);
                EventDto eventDto = new EventDto();
                BeanUtils.copyProperties(oldEventEntity, eventDto);
                return eventDto;
            } else {
                String errorMsg = String.format("userId=%s not authorised to update eventId=%s", updateEventRequest.getRequesterId(), updateEventRequest.getEventId());
                log.error(errorMsg);
                throw new UnauthorizedException(errorMsg);
            }
        } else {
            throw new EventNotFoundException("Event not present with id=" + updateEventRequest.getEventId());
        }
    }

}

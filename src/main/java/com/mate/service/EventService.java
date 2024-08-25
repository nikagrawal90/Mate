package com.mate.service;

import com.mate.entity.EventEntity;
import com.mate.entity.UserEntity;
import com.mate.entity.enums.EventStatus;
import com.mate.exception.*;
import com.mate.model.dto.EventDto;
import com.mate.model.dto.request.AddAttendeeRequest;
import com.mate.model.dto.request.RemoveAttendeeRequest;
import com.mate.model.dto.request.UpdateEventRequest;
import com.mate.model.dto.response.CreateEventResponse;
import com.mate.model.dto.response.RefundDetailsResponse;
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
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    public CreateEventResponse createEvent(EventDto eventDto) throws UserNotFoundException {
        EventEntity eventEntity = new EventEntity();
        BeanUtils.copyProperties(eventDto, eventEntity);

        eventRepository.save(eventEntity);

        return CreateEventResponse.builder().eventId(eventEntity.getEventId()).build();
    }

    public EventEntity getEventById(String eventId) throws EventNotFoundException {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event=%s not found", eventId)));
    }

    public void deleteEvent(String eventId) throws EventNotFoundException {
        if(eventRepository.findById(eventId).isPresent()) {
            eventRepository.deleteById(eventId);
        } else {
            throw new EventNotFoundException(String.format("Event=%s not found", eventId));
        }

    }

    public List<EventDto> getAllEvents() {
        List<EventEntity> eventEntities = eventRepository.findAll();
        List<EventDto> eventDtos = new ArrayList<>();

        eventEntities.forEach(eventEntity -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(eventEntity, eventDto);
            eventDtos.add(eventDto);
        });

        return eventDtos;
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

    public void updateEvent(UpdateEventRequest updateEventRequest) throws UnauthorizedException, EventNotFoundException {
        Optional<EventEntity> oldEventEntityOptional = eventRepository.findById(updateEventRequest.getEventId());

        if(oldEventEntityOptional.isPresent()){
            EventEntity oldEventEntity = oldEventEntityOptional.get();
            if(oldEventEntity.getHostId().equals(updateEventRequest.getRequesterId())) {
                oldEventEntity.setEventStatus(updateEventRequest.getEventStatus());
                oldEventEntity.setLocation(updateEventRequest.getLocation());
                oldEventEntity.setName(updateEventRequest.getName());
                eventRepository.save(oldEventEntity);

                String responseMessage = "Updated event with id=" + updateEventRequest.getEventId();

                log.info(responseMessage);
            } else {
                String errorMsg = String.format("userId=%s not authorised to update eventId=%s", updateEventRequest.getRequesterId(), updateEventRequest.getEventId());
                log.error(errorMsg);
                throw new UnauthorizedException(errorMsg);
            }
        } else {
            throw new EventNotFoundException("Event not present with id=" + updateEventRequest.getEventId());
        }
    }

    public void addAttendee(AddAttendeeRequest addAttendeeRequest) throws EventNotFoundException, UserNotFoundException, PaymentDetailsNotFoundException, PaymentDetailsMismatchException, InsufficientAmountException, PaymentExpiredException, InvalidRequestException {
        EventEntity eventEntity = eventRepository.findById(addAttendeeRequest.getEventId()).orElseThrow(() -> new EventNotFoundException(String.format("Event=%s not found", addAttendeeRequest.getEventId())));

        if(eventEntity.getAttendees().contains(addAttendeeRequest.getAttendeeId())) {
            throw new InvalidRequestException(String.format("Attendee=%s already present in event=%s", addAttendeeRequest.getAttendeeId(), addAttendeeRequest.getEventId()));
        }

        eventEntity.getAttendees().add(addAttendeeRequest.getAttendeeId());
        eventRepository.save(eventEntity);
    }

    public RefundDetailsResponse getRefundDetails(String bookingId) {

    }

    public void removeAttendee(RemoveAttendeeRequest removeAttendeeRequest) throws UserNotFoundException, InvalidRequestException, EventNotFoundException {
        EventEntity eventEntity = eventRepository.findById(removeAttendeeRequest.getEventId()).orElseThrow(() -> new EventNotFoundException(String.format("Event=%s not found", removeAttendeeRequest.getEventId())));
        UserEntity attendee = userService.getUserById(removeAttendeeRequest.getAttendeeId());
        if(!eventEntity.getAttendees().contains(removeAttendeeRequest.getAttendeeId())) {
            throw new InvalidRequestException(String.format("Attendee=%s not present in event=%s", removeAttendeeRequest.getAttendeeId(), removeAttendeeRequest.getEventId()));
        }

        RefundDetailsResponse = getRefundDetails(removeAttendeeRequest.getAttendeeId(), removeAttendeeRequest.getEventId());



    }

}

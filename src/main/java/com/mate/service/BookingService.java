package com.mate.service;

import com.mate.entity.BookingEntity;
import com.mate.entity.EventEntity;
import com.mate.entity.UserEntity;
import com.mate.entity.enums.Status;
import com.mate.exception.*;
import com.mate.model.RefundPolicy;
import com.mate.model.dto.BookingDto;
import com.mate.model.dto.request.ConfirmBookingRequest;
import com.mate.model.dto.response.RefundDetailsResponse;
import com.mate.repository.BookingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@Log4j2
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventService eventService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, EventService eventService, UserService userService, PaymentService paymentService) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
        this.userService = userService;
        this.paymentService = paymentService;
    }

    public BookingDto addBooking(BookingDto bookingDto) {
        BookingEntity bookingEntity = new BookingEntity();

        BeanUtils.copyProperties(bookingDto, bookingEntity);
        bookingEntity.setStatus(Status.PENDING);
        bookingRepository.save(bookingEntity);
        bookingDto.setBookingId(bookingEntity.getBookingId());

        return bookingDto;
    }

    public BookingEntity getBookingById(String bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(String.format("Booking with id=%s not found", bookingId)));
    }

    public void confirmBooking(ConfirmBookingRequest confirmBookingRequest) throws PaymentDetailsNotFoundException, PaymentDetailsMismatchException, PaymentExpiredException, InsufficientAmountException, PaymentFailedException, BookingNotFoundException, EventNotFoundException, UserNotFoundException {
        BookingEntity bookingEntity = getBookingById(confirmBookingRequest.getBookingId());
        UserEntity userEntity = userService.getUserById(bookingEntity.getUserId());
        EventEntity eventEntity = eventService.getEventById(bookingEntity.getEventId());

        paymentService.verifyPaymentForEvent(confirmBookingRequest.getPaymentId(), userEntity, eventEntity, confirmBookingRequest.getBookingId());

        bookingEntity.setStatus(Status.SUCCESS);
    }

    public void cancelBooking(BookingEntity bookingEntity) throws InvalidRequestException, BookingNotFoundException, PaymentDetailsNotFoundException, EventNotFoundException {
        if(!bookingEntity.getStatus().equals(Status.SUCCESS)) {
            throw new InvalidRequestException(String.format("Booking with bookingId=%s not in successful state", bookingEntity.getBookingId()));
        }

        if(bookingEntity.getBookingTimestamp().before(Timestamp.from(Instant.now()))) {
            throw new InvalidRequestException(String.format("Booking with bookingId=%s is of older timestamp", bookingEntity.getBookingId()));
        }

        paymentService.initiateRefund(bookingEntity.getPaymentId());
        bookingEntity.setStatus(Status.CANCELLED);
        bookingRepository.save(bookingEntity);
    }

    public RefundDetailsResponse getRefundDetails(String bookingId) throws BookingNotFoundException, EventNotFoundException {
        BookingEntity bookingEntity = getBookingById(bookingId);
        EventEntity eventEntity = eventService.getEventById(bookingEntity.getEventId());
        RefundPolicy refundPolicy = eventEntity.getRefundPolicy();

        Double amount = bookingEntity.getBookingAmount();
        double refundableAmount = amount*refundPolicy.getPercentageRefundable()/100 - refundPolicy.getAdditionalCharges();
        if(refundableAmount < 0) {
            refundableAmount = 0;
        }
        if(refundableAmount > amount) {
            refundableAmount = amount;
        }
        return RefundDetailsResponse.builder().refundAmount(refundableAmount).refundPolicy(refundPolicy).build();
    }

    public void updateBookingStatus(String bookingId, Status paymentStatus) throws InvalidBookingStateException, BookingNotFoundException {
        BookingEntity bookingEntity = getBookingById(bookingId);
        if(bookingEntity.getStatus().equals(Status.CANCELLED) || bookingEntity.getStatus().equals(Status.FAILED)) {
            throw new InvalidBookingStateException(String.format("Booking is in %s state", bookingEntity.getStatus()));
        }
        bookingEntity.setStatus(paymentStatus);
        bookingRepository.save(bookingEntity);
    }

    public void cancelEvent(String eventId) throws EventNotFoundException {
        eventService.cancelEvent(eventId);
        cancelAllBookingForEvent(eventId);
    }

    private void cancelAllBookingForEvent(String eventId) {
        List<BookingEntity> bookingEntities = bookingRepository.findBookingEntitiesByEventId(eventId);
        for(BookingEntity bookingEntity: bookingEntities) {
            try {
                cancelBooking(bookingEntity);
            } catch (BookingNotFoundException | PaymentDetailsNotFoundException | EventNotFoundException |
                     InvalidRequestException e) {
                log.error("Error cancelling booking with id - " + bookingEntity.getBookingId());
                // TODO: send request to cancel this booking to DLQ
            }
        }
    }

    public List<String> getAllAttendees(String eventId) {
        List<BookingEntity> bookingEntities = bookingRepository.findBookingEntitiesByEventId(eventId);
        return bookingEntities.stream().map(BookingEntity::getUserId).toList();
    }
}

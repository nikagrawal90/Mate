package com.mate.service;

import com.mate.entity.EventEntity;
import com.mate.entity.PaymentDetailsEntity;
import com.mate.entity.UserEntity;
import com.mate.entity.enums.Status;
import com.mate.exception.*;
import com.mate.model.dto.PaymentDetailsDto;
import com.mate.repository.PaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Log4j2
public class PaymentService {
    private static final Integer EXPIRATION_TIME_IN_MINUTES = 15;

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentDetailsDto createPaymentInstance(PaymentDetailsDto paymentDetailsDto) {
        PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
        BeanUtils.copyProperties(paymentDetailsDto, paymentDetailsEntity);

        paymentDetailsEntity.setPaymentStatus(Status.PENDING);

        paymentRepository.save(paymentDetailsEntity);
        paymentDetailsDto.setPaymentId(paymentDetailsEntity.getPaymentId());
        paymentDetailsDto.setPaymentStatus(paymentDetailsEntity.getPaymentStatus());

        return paymentDetailsDto;
    }

    public void verifyPaymentForEvent(String paymentId, UserEntity userEntity, EventEntity eventEntity, String bookingId) throws PaymentDetailsNotFoundException, PaymentDetailsMismatchException, InsufficientAmountException, PaymentExpiredException, PaymentFailedException {
        PaymentDetailsEntity paymentDetailsEntity = paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentDetailsNotFoundException(String.format("Payment=%s not found", paymentId)));

        if(!paymentDetailsEntity.getPaymentStatus().equals(Status.SUCCESS)) {
            throw new PaymentFailedException(String.format("Payment with paymentId=%s is failed", paymentId));
        }

        if(!paymentDetailsEntity.getSenderId().equals(userEntity.getUserId())) {
            throw new PaymentDetailsMismatchException(String.format("Payment userId=%s and requesterId=%s does not match", userEntity.getUserId(), paymentDetailsEntity.getSenderId()));
        }
        if(!paymentDetailsEntity.getBookingId().equals(bookingId)) {
            throw new PaymentDetailsMismatchException(String.format("Payment bookingId=%s and requestedBookingId=%s does not match", paymentDetailsEntity.getBookingId(), bookingId));
        }

        if(eventEntity.getJoiningFee() > paymentDetailsEntity.getAmount()) {
            throw new InsufficientAmountException(String.format("Event=%s has joining fee=%s but paid amount=%s.", eventEntity.getEventId(), eventEntity.getJoiningFee(), paymentDetailsEntity.getAmount()));
        }

        if(isPaymentExpired(paymentDetailsEntity.getTimestamp())) {
            throw new PaymentExpiredException(String.format("Payment with paymentId=%s expired", paymentId));
        }
    }

    public static boolean isPaymentExpired(Timestamp timestampToCheck) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTime = EXPIRATION_TIME_IN_MINUTES * 60 * 1000;
        long thresholdTimeMillis = currentTimeMillis - expirationTime;

        // Create a Timestamp object for the threshold time
        Timestamp thresholdTimestamp = new Timestamp(thresholdTimeMillis);

        // Compare the given timestamp with the threshold timestamp
        return timestampToCheck.before(thresholdTimestamp);
    }

    public void initiateRefund(String paymentId) throws PaymentDetailsNotFoundException {
        PaymentDetailsEntity paymentDetails = getPaymentDetailsById(paymentId);

        // Initiate refund with Payment Gateway
        updatePaymentStatus(paymentDetails, Status.REFUNDED);
    }

    public PaymentDetailsEntity getPaymentDetailsById(String paymentId) throws PaymentDetailsNotFoundException {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentDetailsNotFoundException(String.format("PaymentDetails with paymentId=%s not found", paymentId)));
    }

    public void updatePaymentStatus(PaymentDetailsEntity paymentDetails, Status paymentStatus) {
        paymentDetails.setPaymentStatus(paymentStatus);
        paymentRepository.save(paymentDetails);
    }
}

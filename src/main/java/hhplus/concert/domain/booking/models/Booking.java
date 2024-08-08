package hhplus.concert.domain.booking.models;

import hhplus.concert.api.exception.RestApiException;
import hhplus.concert.domain.concert.models.ConcertOption;
import hhplus.concert.domain.history.payment.models.Payment;
import hhplus.concert.domain.user.models.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static hhplus.concert.api.exception.code.BookingErrorCode.*;
import static hhplus.concert.domain.booking.models.BookingRule.BOOKING_EXPIRY_MINUTES;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDateTime;
    private String concertTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "booking")
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @OneToOne(mappedBy = "booking")
    private Payment payment;

    @Builder
    private Booking(Long id, BookingStatus bookingStatus, LocalDateTime bookingDateTime, String concertTitle, User user, List<BookingSeat> bookingSeats, Payment payment) {
        this.id = id;
        this.bookingStatus = bookingStatus;
        this.bookingDateTime = bookingDateTime;
        this.concertTitle = concertTitle;
        this.user = user;
        this.bookingSeats = bookingSeats;
        this.payment = payment;
    }

    public static Booking buildBooking(ConcertOption concertOption, User user) {
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.INCOMPLETE)
                .bookingDateTime(LocalDateTime.now())
                .concertTitle(concertOption.getConcert().getTitle())
                .user(user)
                .build();
        return booking;
    }

    public void markAsComplete() {
        this.bookingStatus = BookingStatus.COMPLETE;
    }

    public void validateBookingDateTime() {
        if (isBookingDateTimeExpired()) {
            log.info("BookingErrorCode.EXPIRED_BOOKING_TIME 발생");
            throw new RestApiException(EXPIRED_BOOKING_TIME);
        }
    }

    public void validatePendingBooking() {
        if (isBookingDateTimeValid()) {
            log.info("BookingErrorCode.PENDING_BOOKING 발생");
            throw new RestApiException(PENDING_BOOKING);
        }
    }

    public int getTotalPrice() {
        return this.bookingSeats.stream()
                .mapToInt(bs -> bs.getSeat().getPrice())
                .sum();
    }

    public void validatePayer(Long userId) {
        if (user.isNotSameUserId(userId)) {
            throw new RestApiException(INVALID_PAYER);
        }
    }

    public void reserveAllSeats() {
        bookingSeats.stream()
                .map(BookingSeat::getSeat)
                .forEach(seat -> seat.markAsBooked());
    }

    private boolean isBookingDateTimeExpired() {
        return getMinutesSinceBooking() > BOOKING_EXPIRY_MINUTES.toLong();
    }

    private boolean isBookingDateTimeValid() {
        return getMinutesSinceBooking() <= BOOKING_EXPIRY_MINUTES.toLong();
    }

    private long getMinutesSinceBooking() {
        return Duration.between(this.getBookingDateTime(), LocalDateTime.now()).toMinutes();
    }
}

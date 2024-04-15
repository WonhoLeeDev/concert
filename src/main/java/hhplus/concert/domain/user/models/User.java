package hhplus.concert.domain.user.models;

import hhplus.concert.domain.balance.models.BalanceHistory;
import hhplus.concert.domain.booking.model.Booking;
import hhplus.concert.domain.payment.model.Payment;
import hhplus.concert.domain.queue.model.Queue;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class User {

    private Long id;
    private String name;
    private long balance;
    private List<Queue> queues;
    private List<BalanceHistory> balanceHistories;
    private List<Booking> bookings;
    private List<Payment> payments;

    @Builder
    private User(Long id, String name, long balance,
                @Singular List<Queue> queues,
                @Singular List<BalanceHistory> balanceHistories,
                @Singular List<Booking> bookings,
                @Singular List<Payment> payments) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.queues = queues;
        this.balanceHistories = balanceHistories;
        this.bookings = bookings;
        this.payments = payments;
    }
}

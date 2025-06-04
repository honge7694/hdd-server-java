package kr.hhplus.be.server.user.domain;

import kr.hhplus.be.server.user.domain.enums.TransactionType;

import java.time.LocalDateTime;

public class UserHistory {
    private Long id;
    private final Long userId;
    private final int amount;
    private final TransactionType transactionType;
    private final LocalDateTime createdAt;


    public UserHistory(Long userId, int amount, TransactionType transactionType, LocalDateTime createdAt) {
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
    }

    public static UserHistory create(Long userId, Integer amount, TransactionType transactionType, LocalDateTime createdAt) {
        return new UserHistory(userId, amount, transactionType, createdAt);
    }

    /* Getter */
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

package kr.hhplus.be.server.user.domain;

import kr.hhplus.be.server.global.exception.ConflictException;

public class User {
    private Long id;
    private final String name;
    private final String email;
    private final String password;
    private final Address address;
    private int balance;

    // 생성
    public User(String name, String email, String password, Address address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.balance = 0;
    }

    public static User create(String name, String email, String password, Address address) {
        return new User(name, email, password, address);
    }

    // entity -> domain
    public static User create(String name, String email, String password, Address address, int balance) {
        return new User(name, email, password, address, balance);
    }

    public User(String name, String email, String password, Address address, int balance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.balance = balance;
    }

    /* 비즈니스 로직 */
    public void chargeBalance(int amount) {
        balance += amount;
    }

    public void deductBalance(int amount) {
        if (balance - amount < 0) throw new ConflictException("잔액이 부족합니다.");
        balance -= amount;
    }

    /* Getter */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Address getAddress() {
        return address;
    }

    public int getBalance() {
        return balance;
    }

    public User assignId(Long id) {
        User userWithId = new User(this.name, this.email, this.password, this.address, this.balance);
        userWithId.id = id;
        return userWithId;
    }
}

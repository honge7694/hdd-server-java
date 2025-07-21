package kr.hhplus.be.server.userservice.infra.gateway.entity;

import jakarta.persistence.*;

import kr.hhplus.be.server.userservice.domain.Address;
import kr.hhplus.be.server.userservice.domain.User;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;
    private String name;
    private String email;
    private String password;
    private int balance;

    @Embedded
    private Address address;

    protected UserEntity() {}

    public UserEntity(String name, String email, String password, Address address, int balance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.balance = balance;
    }

    public UserEntity(long id, String name, String email, String password, Address address, int balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.balance = balance;
    }

    public static UserEntity fromDomain(User user) {
        return new UserEntity(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getAddress(),
                user.getBalance()
        );
    }

    public User toDomain() {
        return User.create(name, email, password, address, balance).assignId(id);
    }

    // entity 수정 메서드
    public static UserEntity applyDomain(User user) {
        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getAddress(),
                user.getBalance()
        );
    }
}

package kr.hhplus.be.server.userservice.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private int balance;
    private AddressResponseDto address;

    public UserResponseDto(Long id, String name, String email, int balance, AddressResponseDto address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.address = address;
    }
}
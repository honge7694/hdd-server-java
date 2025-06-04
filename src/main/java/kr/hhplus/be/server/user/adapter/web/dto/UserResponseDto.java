package kr.hhplus.be.server.user.adapter.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private AddressResponseDto address;

    public UserResponseDto(Long id, String name, String email, AddressResponseDto address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }
}
package kr.hhplus.be.server.userservice.presentation.dto;

import kr.hhplus.be.server.userservice.domain.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDto {
    private String city;
    private String street;
    private String zipcode;

    public AddressResponseDto(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public static AddressResponseDto from(Address address) {
        return new AddressResponseDto(address.getCity(), address.getStreet(), address.getZipcode());
    }
}
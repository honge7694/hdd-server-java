package kr.hhplus.be.server.user.adapter.web.dto;

import kr.hhplus.be.server.user.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    private String city;
    private String street;
    private String zipcode;

    public Address toDomain() {
        return Address.create(city, street, zipcode);
    }
}
package kr.hhplus.be.server.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.userservice.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    @Schema(description = "도시", example = "서울")
    private String city;

    @Schema(description = "거리", example = "강남대로")
    private String street;

    @Schema(description = "우편번호", example = "12345")
    private String zipcode;

    public Address toDomain() {
        return Address.create(city, street, zipcode);
    }
}
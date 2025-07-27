package kr.hhplus.be.server.userservice.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeductBalanceRequestDto {
    private Long userId;
    private int amount;
    private String correlationId;
}

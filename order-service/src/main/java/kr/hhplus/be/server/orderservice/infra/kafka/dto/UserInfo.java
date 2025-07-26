package kr.hhplus.be.server.orderservice.infra.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private int balance;
    private String correlationId; // 요청-응답 패턴을 위한 ID
}

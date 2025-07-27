package kr.hhplus.be.server.userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.userservice.application.usecase.command.UserDeductBalanceCommand;
import kr.hhplus.be.server.userservice.application.usecase.port.in.UserDeductBalanceInput;
import kr.hhplus.be.server.userservice.application.usecase.port.in.UserFinder;
import kr.hhplus.be.server.userservice.application.usecase.result.UserResult;
import kr.hhplus.be.server.userservice.kafka.dto.UserDeductBalanceRequestDto;
import kr.hhplus.be.server.userservice.kafka.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaListener {

    private final UserFinder userFinder;
    private final UserDeductBalanceInput userDeductBalanceInput;
    private final UserKafkaProducer userKafkaProducer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-request", groupId = "user-group")
    public void getUser(String message) {
        log.info("user-request message: {}", message);

        String correlationId = null;

        try {
            UserRequestDto request = objectMapper.readValue(message, UserRequestDto.class);
            correlationId = request.getCorrelationId();
            UserResult userResult = userFinder.findById(request.getUserId());
            userKafkaProducer.send("user-response", userResult, correlationId);
        } catch (IOException e) {
            log.error("user-response fail. [correlationId={}]", correlationId, e);
        }
    }

    @KafkaListener(topics = "deduct-balance-request", groupId = "user-group")
    public void deductBalance(String message) {
        log.info("deduct-balance-request message: {}", message);

        String correlationId = null;

        try {
            UserDeductBalanceRequestDto request = objectMapper.readValue(message, UserDeductBalanceRequestDto.class);
            correlationId = request.getCorrelationId();
            userDeductBalanceInput.deductUserBalance(new UserDeductBalanceCommand(request.getUserId(), request.getAmount()));
            userKafkaProducer.send("deduct-balance-response", "success", correlationId);
        } catch (Exception e) {
            log.error("deduct-balance-response fail. [correlationId={}]", correlationId, e);
            userKafkaProducer.send("deduct-balance-response", "fail", correlationId);
        }
    }
}

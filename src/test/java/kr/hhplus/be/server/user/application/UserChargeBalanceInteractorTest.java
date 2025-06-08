package kr.hhplus.be.server.user.application;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.user.application.interactor.UserChargeBalanceInteractor;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.application.usecase.command.UserChargeBalanceCommand;
import kr.hhplus.be.server.user.application.usecase.command.UserRegisterCommand;
import kr.hhplus.be.server.user.application.usecase.port.out.UserOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserChargeBalanceInteractorTest {

    @InjectMocks
    private UserChargeBalanceInteractor userChargeBalanceInteractor;
    @Mock
    UserRepository userRepository;

    private UserRegisterCommand registerCommand;
    private UserOutput presenter;
    private UserChargeBalanceCommand chargeCommand;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.create("city", "street", "zipcode");
        registerCommand = new UserRegisterCommand("test", "test@email.com", "test", address);
        chargeCommand = new UserChargeBalanceCommand(1L, 1000);
        presenter = mock(UserOutput.class);
    }

    @Test
    @DisplayName("유저 포인트 충전")
    public void chargeUserBalance() {
        // given
        User newUser = User.create("test", "test@email.com", "test", address);
        newUser.assignId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        when(userRepository.update(any(User.class))).thenReturn(newUser);

        // when
        userChargeBalanceInteractor.chargeUserBalance(chargeCommand, presenter);

        // then
        assertEquals(1000, newUser.getBalance());
    }

    @Test
    @DisplayName("유저 포인트 충전 실패 - 유저 조회 실패")
    public void chargeUserBalance_Fail() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            userChargeBalanceInteractor.chargeUserBalance(chargeCommand, presenter);
        });

        // then
        assertEquals(notFoundException.getMessage(), "유저를 찾을 수 없습니다.");
    }
}
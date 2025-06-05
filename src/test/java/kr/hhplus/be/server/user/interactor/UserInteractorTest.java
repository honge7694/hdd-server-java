package kr.hhplus.be.server.user.interactor;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.user.adapter.gateway.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.usecase.in.ChargeUserBalanceCommand;
import kr.hhplus.be.server.user.usecase.in.RegisterUserCommand;
import kr.hhplus.be.server.user.usecase.out.RegisterUserOutput;
import kr.hhplus.be.server.user.usecase.out.RegisterUserResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInteractorTest {

    @InjectMocks
    private UserInteractor userInteractor;
    @Mock
    UserRepository userRepository;

    private RegisterUserCommand command;
    private RegisterUserOutput presenter;
    private ChargeUserBalanceCommand chargeCommand;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.create("city", "street", "zipcode");
        command = new RegisterUserCommand("test", "test@email.com", "test", address);
        chargeCommand = new ChargeUserBalanceCommand(1L, 1000);
        presenter = mock(RegisterUserOutput.class);
    }

    @Test
    @DisplayName("유저 등록 성공")
    public void registerUser() {
        // given
        User newUser = User.create("test", "test@email.com", "test", address);
        newUser.assignId(1L);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        userInteractor.registerUser(command, presenter);

        // then
        verify(presenter).ok(new RegisterUserResult(newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getBalance(), newUser.getAddress()));
    }

    @Test
    @DisplayName("유저 등록 실패 - 이메일 중복")
    public void registerUser_DuplicateEmail_Fail() {
        // given
        when(userRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(User.create("test", "test@email.com", "test", address)));

        // when
        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            userInteractor.registerUser(command, presenter);
        });

        // then
        assertEquals("이미 사용 중인 이메일입니다.", conflictException.getMessage());
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
        userInteractor.chargeUserBalance(chargeCommand, presenter);

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
            userInteractor.chargeUserBalance(chargeCommand, presenter);
        });

        // then
        assertEquals(notFoundException.getMessage(), "유저를 찾을 수 없습니다.");
    }
}
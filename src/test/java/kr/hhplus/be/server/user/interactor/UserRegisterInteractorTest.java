package kr.hhplus.be.server.user.interactor;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.user.adapter.gateway.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.usecase.in.UserChargeBalanceCommand;
import kr.hhplus.be.server.user.usecase.in.UserRegisterCommand;
import kr.hhplus.be.server.user.usecase.out.UserOutput;
import kr.hhplus.be.server.user.usecase.out.UserResult;
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
class UserRegisterInteractorTest {

    @InjectMocks
    private UserRegisterInteractor userRegisterInteractor;
    @Mock
    UserRepository userRepository;

    private UserRegisterCommand command;
    private UserOutput presenter;
    private UserChargeBalanceCommand chargeCommand;
    private Address address;

    @BeforeEach
    void setUp() {
        address = Address.create("city", "street", "zipcode");
        command = new UserRegisterCommand("test", "test@email.com", "test", address);
        chargeCommand = new UserChargeBalanceCommand(1L, 1000);
        presenter = mock(UserOutput.class);
    }

    @Test
    @DisplayName("유저 등록 성공")
    public void registerUser() {
        // given
        User newUser = User.create("test", "test@email.com", "test", address);
        newUser.assignId(1L);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        userRegisterInteractor.registerUser(command, presenter);

        // then
        verify(presenter).ok(new UserResult(newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getBalance(), newUser.getAddress()));
    }

    @Test
    @DisplayName("유저 등록 실패 - 이메일 중복")
    public void registerUser_DuplicateEmail_Fail() {
        // given
        when(userRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(User.create("test", "test@email.com", "test", address)));

        // when
        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            userRegisterInteractor.registerUser(command, presenter);
        });

        // then
        assertEquals("이미 사용 중인 이메일입니다.", conflictException.getMessage());
    }
}
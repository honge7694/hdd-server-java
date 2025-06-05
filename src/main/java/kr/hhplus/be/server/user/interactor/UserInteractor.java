package kr.hhplus.be.server.user.interactor;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.user.adapter.gateway.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.usecase.in.ChargeUserBalanceCommand;
import kr.hhplus.be.server.user.usecase.in.RegisterUserCommand;
import kr.hhplus.be.server.user.usecase.in.RegisterUserInput;
import kr.hhplus.be.server.user.usecase.out.RegisterUserOutput;
import kr.hhplus.be.server.user.usecase.out.RegisterUserResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInteractor implements RegisterUserInput {

    private final UserRepository userRepository; // 내부 포트

    public UserInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void registerUser(RegisterUserCommand command, RegisterUserOutput presenter) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new ConflictException("이미 사용 중인 이메일입니다.");
        }
        User user = User.create(command.name(), command.email(), command.password(), command.address());
        User saved = userRepository.save(user);
        presenter.ok(new RegisterUserResult(
                saved.getId(), saved.getName(), saved.getEmail(),  saved.getBalance(), saved.getAddress()
        ));
    }

    @Override
    @Transactional
    public void chargeUserBalance(ChargeUserBalanceCommand command, RegisterUserOutput output) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        user.chargeBalance(command.amount());
        userRepository.update(user);
    }
}

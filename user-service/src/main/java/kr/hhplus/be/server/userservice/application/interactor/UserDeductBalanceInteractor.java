package kr.hhplus.be.server.userservice.application.interactor;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.userservice.application.usecase.command.UserDeductBalanceCommand;
import kr.hhplus.be.server.userservice.application.usecase.port.in.UserDeductBalanceInput;
import kr.hhplus.be.server.userservice.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.userservice.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDeductBalanceInteractor implements UserDeductBalanceInput {

    private final UserRepository userRepository;

    public UserDeductBalanceInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void deductUserBalance(UserDeductBalanceCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        user.deductBalance(command.amount());
        userRepository.update(user);
    }
}

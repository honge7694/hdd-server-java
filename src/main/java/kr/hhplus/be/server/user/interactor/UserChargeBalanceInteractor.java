package kr.hhplus.be.server.user.interactor;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.user.adapter.gateway.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.usecase.in.UserChargeBalanceCommand;
import kr.hhplus.be.server.user.usecase.in.UserChargeBalanceInput;
import kr.hhplus.be.server.user.usecase.out.UserOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserChargeBalanceInteractor implements UserChargeBalanceInput {

    private final UserRepository userRepository;

    public UserChargeBalanceInteractor(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void chargeUserBalance(UserChargeBalanceCommand command, UserOutput output) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        user.chargeBalance(command.amount());
        userRepository.update(user);
    }
}

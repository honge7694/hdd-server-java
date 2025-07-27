package kr.hhplus.be.server.userservice.application.usecase.port.in;

import kr.hhplus.be.server.userservice.application.usecase.result.UserResult;

public interface UserFinder {
    UserResult findById(Long userId);
}

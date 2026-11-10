package com.ranking.ranking_server.service;

import com.ranking.ranking_server.domain.User;
import com.ranking.ranking_server.exception.AuthenticationException;
import com.ranking.ranking_server.exception.DuplicateUserException;
import com.ranking.ranking_server.repository.UserRepository;
import com.ranking.ranking_server.security.JwtTokenProvider;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signUp(String userId, String password) {
        if (userRepository.findById(userId).isPresent()) {
            throw new DuplicateUserException("이미 중복된 아이디 입니다.");
        }
        String hashResult = bCryptPasswordEncoder.encode(password);
        User newUser = new User(userId, hashResult);
        userRepository.save(newUser);
    }

    public String login(String userId, String password) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException(LOGIN_FAILED));

        if (!bCryptPasswordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException(LOGIN_FAILED);
        }

        return jwtTokenProvider.createToken(userId);
    }

    private static final String LOGIN_FAILED = "아이디 또는 비밀번호가 올바르지 않습니다.";
}

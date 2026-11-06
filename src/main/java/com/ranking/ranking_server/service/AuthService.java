package com.ranking.ranking_server.service;

import com.ranking.ranking_server.domain.User;
import com.ranking.ranking_server.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void signUp(String userId, String password) {
        if (userRepository.findById(userId).isPresent()) {
            throw new IllegalStateException("중복된 아이디 입니다.");
        }
        String hashResut = bCryptPasswordEncoder.encode(password);
        User newUser = new User(userId, hashResut);
        userRepository.save(newUser);
    }
}

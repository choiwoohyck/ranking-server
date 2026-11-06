package com.ranking.ranking_server.service;

import com.ranking.ranking_server.domain.User;
import com.ranking.ranking_server.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void signUp(String userId, String password)
    {
       if(userRepository.findById(userId).isPresent()) {
           throw new IllegalStateException("중복된 아이디 입니다.");
       }

       User newUser = new User(userId,password);
       userRepository.save(newUser);
    }
}

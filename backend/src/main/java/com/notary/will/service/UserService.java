package com.notary.will.service;

import com.notary.will.dto.LoginRequest;
import com.notary.will.dto.LoginResponse;
import com.notary.will.dto.RegisterRequest;
import com.notary.will.entity.User;
import com.notary.will.enums.UserRole;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.UserRepository;
import com.notary.will.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!user.getEnabled()) {
            throw new BusinessException("用户已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User userUpdate) {
        User user = getUserById(id);
        if (userUpdate.getRealName() != null) user.setRealName(userUpdate.getRealName());
        if (userUpdate.getPhone() != null) user.setPhone(userUpdate.getPhone());
        if (userUpdate.getEnabled() != null) user.setEnabled(userUpdate.getEnabled());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

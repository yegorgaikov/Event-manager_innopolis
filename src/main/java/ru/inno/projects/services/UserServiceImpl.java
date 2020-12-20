package ru.inno.projects.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inno.projects.models.Event;
import ru.inno.projects.models.Role;
import ru.inno.projects.models.User;
import ru.inno.projects.repos.UserRepo;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final EventMailServise mailServise;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepo userRepo,
                           EventMailServise mailServise,
                           PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.mailServise = mailServise;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Start method getAllUser from UserServiceImpl");
        return userRepo.findAll();
    }

    @Override
    public boolean addUser(User user) {
        log.info("Start method addUser from UserServiceImpl");

        if (isUserExists(user)) return false;

        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

        sendMessage(user);

        return true;
    }

    public boolean isUserExists(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (!Objects.equals(userFromDb, null)) {
            log.info("Пользователь пытается зарегистрироваться под уже существующим аккаунтом");
            return true;
        }
        return false;
    }

    @Override
    public User getUserByLogin(String login) {
        return null;
    }

    @Override
    public User getUserByName(String name) {
        return null;
    }

    @Override
    public User getUserById(BigDecimal userId) {
        return null;
    }

    @Transactional
    @Override
    public Set<User> getAllUsersByEvent(Event event) {
        return new HashSet<>(userRepo.findAllByEvents(event));
    }

    @Override
    public boolean updateUserRoles(User user, Map<String, String> form) {
        log.info("Start method updateUserRoles from UserServiceImpl");

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);

        return true;
    }

    @Override
    public boolean updateUser(User user, String password, String phoneNumber, String email) {
        log.info("Start method updateUser from UserServiceImpl");
        String userEmail = user.getEmail();

        boolean isEmailChanged = (email != null && !email.equals(userEmail)) ||
                (userEmail != null && !userEmail.equals(email));

        if (isEmailChanged) {
            user.setEmail(email);
            if (email != null && email.isEmpty()) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (!phoneNumber.isEmpty()) {
            user.setPhoneNumber(phoneNumber);
        }

        userRepo.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }

        return true;
    }

    private void sendMessage(User user) {
        log.info("Start method sendMessage from UserServiceImpl");

        if (!user.getEmail().isEmpty()) {
            final String message = String.format(
                    "Привет, %s! \n" +
                            "Добро пожаловать в Event Manager. Для подтверждения регистрации перейдите по ссылке: " +
                            "http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailServise.send(user.getEmail(), "Activation code", message);
        }
    }

    @Override
    public boolean deleteUser(User user) {
        return false;
    }

    @Override
    public boolean deleteUserById(BigDecimal userId) {
        return false;
    }

    @Override
    public boolean activateUser(String code) {
        log.info("Start method activateUser from UserServiceImpl");

        User user = userRepo.findByActivationCode(code);
        if (Objects.equals(user, null)) {
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Start method loadUserByUsername from UserServiceImpl");
        User user = userRepo.findByUsername(userName);
        return user != null ? user : new User();
    }
}

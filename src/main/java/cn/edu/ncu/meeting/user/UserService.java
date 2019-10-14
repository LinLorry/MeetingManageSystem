package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.user.model.User;
import cn.edu.ncu.meeting.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Value("${Manage.salt}")
    private String salt;

    private static BCryptPasswordEncoder encode = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    boolean addUser(String username, String name, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return false;
        }
        String hash = encode.encode(salt + password + salt);
        user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(hash);

        userRepository.save(user);

        return true;
    }

    boolean checkPassword(User user, String password) {
        return encode.matches(salt + password + salt, user.getPassword());
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User doesn't exits.");
        }
        return user;
    }
}

package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.until.MD5Tool;
import cn.edu.ncu.meeting.user.model.User;
import cn.edu.ncu.meeting.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Value("${Manage.salt}")
    private String salt;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    boolean addUser(String username, String name, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return false;
        }
        String hash = MD5Tool.encode(salt + password + salt);
        user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(hash);

        userRepository.save(user);

        return true;
    }

    boolean checkPassword(User user, String password) {
        return user.getPassword().compareTo(
                MD5Tool.encode(salt + password + salt)) == 0;
    }

    User getUser(String username) {
        return userRepository.findByUsername(username);
    }


}
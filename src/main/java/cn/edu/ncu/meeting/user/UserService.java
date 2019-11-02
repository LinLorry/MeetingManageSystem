package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.user.model.User;
import cn.edu.ncu.meeting.user.repo.UserRepository;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User Service
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.security.core.userdetails.UserDetailsService
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Value("${Manage.salt}")
    private String salt;

    private static BCryptPasswordEncoder encode = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Add user service.
     * @param json have new user data.
     * @return new user.
     */
    User addUser(JSONObject json) {
        User user = new User();

        String hash = encode.encode(salt + json.getString("password").trim() + salt);

        user.setUsername(json.getString("username"));
        user.setName(json.getString("name"));
        user.setPassword(hash);
        user.setIdCard(json.getString("idCard"));
        user.setSex(json.getBooleanValue("sex"));
        user.setOrganization(json.getString("organization"));
        user.setPhoneNumber(json.getString("phoneNumber"));

        userRepository.save(user);
        return user;
    }

    /**
     * Check User exists by username
     * @param username the username
     * @return if user exists return true else return false.
     */
    boolean checkUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Update User
     * @param user the user will be update
     * @param json the user data.
     */
    void updateUser(User user, JSONObject json) {
        user.setName(json.getString("name"));
        user.setIdCard(json.getString("idCard"));
        user.setSex(json.getBooleanValue("sex"));
        user.setOrganization(json.getString("organization"));
        user.setPhoneNumber(json.getString("phoneNumber"));

        userRepository.save(user);
    }

    /**
     * Check User password.
     * @param user be checked user.
     * @param password be checked password.
     * @return if password correct return true else return false.
     */
    boolean checkPassword(User user, String password) {
        return encode.matches(salt + password + salt, user.getPassword());
    }

    /**
     * Update User Password
     * @param user the user
     * @param newPassword the new password
     */
    void updateUserPassword(User user, String newPassword) {
        user.setPassword(
                encode.encode(salt + newPassword.trim() + salt)
        );
        userRepository.save(user);
    }

    /**
     * Load a user by username.
     * @param username the username.
     * @return the user.
     * @throws UsernameNotFoundException if user is not exits throw UsernameNotFoundException.
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User doesn't exits.");
        }
        return user;
    }
}

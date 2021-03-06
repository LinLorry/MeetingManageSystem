package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.user.model.User;
import cn.edu.ncu.meeting.user.repo.UserRepository;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
        user.setGender(json.getBooleanValue("gender"));
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
     * @param id the user will be update id.
     * @param json the user data.
     */
    User updateUser(long id, JSONObject json) {
        User user = loadUserById(id);

        user.setName(json.getString("name"));
        user.setIdCard(json.getString("idCard"));
        user.setGender(json.getBooleanValue("gender"));
        user.setOrganization(json.getString("organization"));
        user.setPhoneNumber(json.getString("phoneNumber"));

        return userRepository.save(user);
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
     * Load a User By id.
     * @param id the user id.
     * @return the user.
     * @throws NoSuchElementException if the user is not exits throw this exception.
     */
    User loadUserById(long id) throws NoSuchElementException {
        return userRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Load a user by username.
     * @param username the username.
     * @return the user.
     * @throws UsernameNotFoundException if user is not exits throw UsernameNotFoundException.
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exits."));
    }

    /**
     * Remove User By Id
     * @param id the user id.
     */
    void removeUserById(long id) {
        userRepository.deleteById(id);
    }
}

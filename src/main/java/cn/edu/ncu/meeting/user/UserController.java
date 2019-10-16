package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.until.TokenUntil;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final TokenUntil tokenUntil;

    private final UserService userService;

    public UserController(TokenUntil tokenUntil, UserService userService) {
        this.tokenUntil = tokenUntil;
        this.userService = userService;
    }

    /**
     * Registry api
     * @param request {
     *      "username": username: String,
     *      "password": password: String,
     *      "name": name: String
     * }
     * @return if registry success return {
     *     "status": 1,
     *     "message": "Create User Success"
     * } else return {
     *     "status: 0,
     *     "message": message: String
     * }
     */
    @ResponseBody
    @PostMapping("/registry")
    public JSONObject registry(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        if (userService.addUser(
                request.getString("username"),
                request.getString("name"),
                request.getString("password")
        )) {
            response.put("status", 1);
            response.put("message", "Create User Success");
        } else {
            response.put("status", 0);
            response.put("message", "Create User Fail");
        }

        return response;
    }

    /**
     * User login, get token api
     * @param request {
     *      "username": username: String,
     *      "password": password: String,
     * }
     * @return if login success return {
     *     "status": 1,
     *     "message": "Login success",
     *     "token": token: String
     * } else return {
     *     "status": 0,
     *     "message": message: String
     * }
     */
    @ResponseBody
    @PostMapping("/token")
    public JSONObject token(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String username = request.getString("username");
        String password = request.getString("password");

        try {
            User user = userService.loadUserByUsername(username);
            if (userService.checkPassword(user, password)) {
                response.put("status", 1);
                response.put("message", "Login success");
                response.put("token", tokenUntil.generateToken(user));
            } else {
                response.put("status", 0);
                response.put("message", "Wrong password.");
            }
        } catch (UsernameNotFoundException e) {
            response.put("status", 0);
            response.put("message", "The user doesn't exist.");
        }

        return response;
    }

    /**
     * Get user self profile api.
     * @return user profile.
     */
    @ResponseBody
    @GetMapping("profile")
    public User profile() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}

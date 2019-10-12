package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.until.TokenUntil;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final TokenUntil tokenUntil;

    private final UserService userService;

    public UserController(TokenUntil tokenUntil, UserService userService) {
        this.tokenUntil = tokenUntil;
        this.userService = userService;
    }

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

    @ResponseBody
    @PostMapping("/token")
    public JSONObject token(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String username = request.getString("username");
        String password = request.getString("password");

        User user = userService.getUser(username);
        if (user != null) {
            if (userService.checkPassword(user, password)) {
                response.put("status", 1);
                response.put("message", "Login success");
                response.put("token", tokenUntil.generateToken(user));
            } else {
                response.put("status", 0);
                response.put("message", "Wrong password.");
            }
        } else {
            response.put("status", 0);
            response.put("message", "The user doesn't exist.");
        }

        return response;
    }
}

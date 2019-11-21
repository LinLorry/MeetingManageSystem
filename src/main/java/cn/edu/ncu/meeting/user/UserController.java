package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.util.SecurityUtil;
import cn.edu.ncu.meeting.util.TokenUtil;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * User Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final TokenUtil tokenUtil;

    private final UserService userService;

    private final static List<Map<String, String>> menus;

    private final static List<Map<String, String>> adminMenus;

    static {
        menus = new ArrayList<>();
        Map<String, String> map;

        String[] fieldAndUrl = {
                "首页", "/index.html",
                "个人信息", "/profile.html",
                "创建会议", "/create.html",
                "查询会议", "/query.html",
                "我的会议", "/myMeetings.html",
                "我参加的会议", "/myJoinMeetings.html"
        };

        for (int i = 0; i < fieldAndUrl.length - 1; i+=2) {
            map = new HashMap<>();
            map.put("name", fieldAndUrl[i]);
            map.put("url", fieldAndUrl[i + 1]);
            menus.add(map);
        }

        adminMenus = new ArrayList<>(menus);

        map = new HashMap<>();
        map.put("name", "系统管理");
        map.put("url", "/admin.html");

        adminMenus.add(map);
    }

    public UserController(TokenUtil tokenUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
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
     *     "message": "Create User Success",
     *     "data": {
     *         "id": user id: long,
     *         "username": username: String,
     *         "name": name: String,
     *         "gender": user gender: boolean (false: male, true: female)
     *         "organization": user organization: String,
     *         "phoneNumber": user phone number: String,
     *         "idCard": user id card number: String
     *     }
     * } else return {
     *     "status: 0,
     *     "message": message: String
     * }
     */
    @ResponseBody
    @PostMapping("/registry")
    public JSONObject registry(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        try {
            response.put("data", userService.addUser(request));
            response.put("status", 1);
            response.put("message", "Registry Success");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            if (userService.checkUserByUsername(request.getString("username"))) {
                response.put("message", "UserName Exist.");
            } else {
                response.put("message", "Registry Failed.");
            }
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
                response.put("token", tokenUtil.generateToken(user));
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
     * @return {
     *     "status": 1,
     *     "message": "Get profile success.",
     *     "data": {
     *         "id": user id: long,
     *         "username": username: String,
     *         "name": name: String,
     *         "gender": user gender: boolean (false: male, true: female)
     *         "organization": user organization: String,
     *         "phoneNumber": user phone number: String,
     *         "idCard": user id card number: String
     *     }
     * }
     */
    @ResponseBody
    @GetMapping("/profile")
    public JSONObject getProfile() {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get profile success.");
        response.put("data", SecurityUtil.getUser());

        return response;
    }

    /**
     * Update user profile api.
     * @param request {
     *      "name": name: String
     * }
     * @return if update user profile success return {
     *     "status": 1,
     *     "message": "Update profile success.",
     *     "data": {
     *         "username": username: String,
     *         "name": name: String
     *     }
     * } else return {
     *     "status": 0,
     *     "message": "Update profile failed."
     * }
     */
    @ResponseBody
    @PostMapping("/profile")
    public JSONObject editProfile(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        try {
            User user = userService.updateUser(SecurityUtil.getUserId(), request);
            response.put("status", 1);
            response.put("message", "Update profile success.");
            response.put("data", user);
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "Update profile failed.");
        }

        return response;
    }

    /**
     * Edit password api.
     * @param request {
     *      "oldPassword": oldPassword: String,
     *      "newPassword": newPassword: String
     * }
     * @return if edit password success return {
     *     "status": 1,
     *     "message": "Edit Password Success"
     * } else if old password wrong return {
     *     "status": 0,
     *     "message": "Old Password Wrong"
     * } else return {
     *     "status": 0,
     *     "message": "Edit Password Failed"
     * }
     */
    @ResponseBody
    @PostMapping("/password")
    public JSONObject editPassword(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        String oldPassword = request.getString("oldPassword");
        String newPassword = request.getString("newPassword");

        User user = SecurityUtil.getUser();

        try {
            if (userService.checkPassword(user, oldPassword)) {
                userService.updateUserPassword(user, newPassword);
                response.put("status", 1);
                response.put("message", "Edit Password Success");
            } else {
                response.put("status", 0);
                response.put("message", "Old Password Wrong");
            }
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "Edit Password Failed");
        }

        return response;
    }

    /**
     * Get Menus Api.
     * @return {
     *     "status": 1,
     *     "message": "Get Menus Success!",
     *     "data": [
     *          {
     *              "name": name: String,
     *              "url": url: String
     *          },
     *          ...
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/menus")
    public JSONObject getMenus() {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get Menus Success!");
        if (!(SecurityUtil.getAuthorities().isEmpty())) {
            response.put("data", adminMenus);
        } else {
            response.put("data", menus);
        }

        return response;
    }

    /**
     * Get Meeting Api.
     * @return {
     *     "status": 1,
     *     "message": "Get meetings success.",
     *     "data": [
     *          {
     *              meeting data
     *          },
     *          ...
     *     ]
     * }
     */
    @GetMapping("/meetings")
    @ResponseBody
    public JSONObject getMeetings() {
        JSONObject response = new JSONObject();
        User user = userService.loadUserById(SecurityUtil.getUserId());

        response.put("status", 1);
        response.put("message", "Get meetings success.");
        response.put("data", user.getMyMeetings());

        return response;
    }

    /**
     * Get Join Meeting Api.
     * @return {
     *     "status": 1,
     *     "message": "Get join meetings success.",
     *     "data": [
     *          {
     *              "id": meeting id: long,
     *              "name": meeting name: String,
     *              "location": location: String,
     *              "time": meeting hold time: Timestamp,
     *              "hotel": meeting hold hotel: String,
     *              "comment": meeting comment: String,
     *              "checkIn": have user check in: Boolean
     *          },
     *          ...
     *     ]
     * }
     */
    @GetMapping("/join")
    @ResponseBody
    public JSONObject getJoinMeeting() {
        JSONObject response = new JSONObject();
        User user = userService.loadUserById(SecurityUtil.getUserId());

        response.put("status", 1);
        response.put("message", "Get join meetings success.");
        response.put("data", user.getJoinMeetings());

        return response;
    }
}

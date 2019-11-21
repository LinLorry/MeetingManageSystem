package cn.edu.ncu.meeting.user;

import cn.edu.ncu.meeting.meeting.MeetingService;
import cn.edu.ncu.meeting.meeting.model.Meeting;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Admin Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final MeetingService meetingService;

    public AdminController(UserService userService, MeetingService meetingService) {
        this.userService = userService;
        this.meetingService = meetingService;
    }

    @ResponseBody
    @GetMapping("/user")
    public JSONObject getUser(@RequestParam String username) {
        JSONObject response = new JSONObject();

        try {
            User user = userService.loadUserByUsername(username);
            response.put("status", 1);
            response.put("message", "Get user information success.");
            response.put("data", user);
        } catch (UsernameNotFoundException e) {
            response.put("status", 0);
            response.put("message", "This user not exist.");
        }

        return response;
    }

    @ResponseBody
    @DeleteMapping("/user")
    public JSONObject removeUser(@RequestParam long id) {
        JSONObject response = new JSONObject();

        userService.removeUserById(id);

        response.put("status", 1);
        response.put("message", "Delete user success");

        return response;
    }

    /**
     * Delete Meeting Api
     * @param id the meeting id.
     * @return {
     *     "status": 1,
     *     "message": "Delete meeting success."
     * }
     */
    @ResponseBody
    @DeleteMapping("/meeting")
    public JSONObject deleteMeeting(@RequestParam long id) {
        JSONObject response = new JSONObject();

        meetingService.removeMeetingById(id);
        response.put("status", 1);
        response.put("message", "Delete meeting success.");

        return response;
    }

    /**
     * Get Meeting Join User Api.
     * @param id the meeting id.
     * @return if meeting exist return {
     *     "status": 1,
     *     "message": "Get meeting join user success."
     * } else if meeting not exist return {
     *     "status": 0,
     *     "message": "This meeting not exist."
     * }
     */
    @ResponseBody
    @GetMapping("/join")
    public JSONObject getMeetingJoinUser(@RequestParam long id) {
        JSONObject response = new JSONObject();

        try {
            Meeting meeting = meetingService.loadMeetingById(id);
            response.put("status", 1);
            response.put("message", "Get meeting join user success.");
            response.put("data", meeting.getJoinUserSet());
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This meeting not exist.");
        }

        return response;
    }
}

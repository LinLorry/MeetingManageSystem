package cn.edu.ncu.meeting.meeting;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import cn.edu.ncu.meeting.meeting.model.MeetingJoinUser;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Meeting Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    /**
     * Create Meeting Api
     * @param request {
     *      "name": meeting's name: String, not null
     *      "time": meeting's hold time: Timestamp, not null
     *      "location": where meeting will be hold: String, not null,
     *      "hotel": meeting's hotel: String,
     *      "comment": meeting's comment, String
     *      "star": who will be invited, String
     *      "needName": It is join meeting need Name?: Boolean, default false,
     *      "needOrganization": It is join meeting need Organization?: Boolean, default false,
     *      "needIdCard": It is join meeting need Id Card?: Boolean, default false,
     *      "needParticipateTime": It is join meeting need Participate Time?: Boolean, default false,
     *      "needGender": It is join meeting need Gender?: Boolean, default false
     * }
     * @return if create meeting success return {
     *     "status": 1,
     *     "message": "Create Meeting Success."
     *     "data": {
     *         meeting data
     *     }
     * } else return {
     *     "status": 0,
     *     "message": "message"
     * }
     */
    @ResponseBody
    @PostMapping("/create")
    public JSONObject create(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            response.put("data", meetingService.addMeeting(request, user));
            response.put("status", 1);
            response.put("message", "Create Meeting Success.");
        } catch (Exception e) {
            response.put("status", 0);
            if (request.getString("name") == null) {
                response.put("message", "Name can't be null.");
            } else if (request.getString("time") == null) {
                response.put("message", "Time can't be null.");
            } else if (request.getString("location") == null) {
                response.put("message", "Location can't be null.");
            } else {
                logger.error(e);
                response.put("message", "Create Meeting Failed.");
            }
        }

        return response;
    }

    /**
     * User Join Meeting Api
     * @param request {
     *      "meetingId": meetingId: String,
     *      "needHotel": It is join user need hotel?: Boolean, default false,
     *      "participateTime": participate time: Timestamp: if meeting need participate time not null, default null
     * }
     * @return if join success return {
     *     "status": 1,
     *     "message": "Sign Up Success."
     * } else if hold user want join his meeting return {
     *     "status": 0,
     *     "message": "Hold User can't join meeting."
     * } else if meeting need participate time but participate time is null return {
     *     "status": 0,
     *     "message": "Need Participate Time."
     * } else if meeting isn't exist return {
     *     "status": 0,
     *     "message": "This Meeting isn't exist."
     * } else return {
     *     "status": 0,
     *     "message": "Sign Up Failed."
     * }
     */
    @ResponseBody
    @PostMapping("/join")
    public JSONObject join(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Meeting meeting = meetingService.loadMeetingById(request.getInteger("meetingId"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean needHotel = request.getBooleanValue("needHotel");
        Timestamp participate = request.getTimestamp("participateTime");

        try {
            if (meeting.getHoldUser().getId() == user.getId()) {
                response.put("status", 0);
                response.put("message", "Hold User can't join meeting.");
            } else if (participate == null && meeting.isNeedParticipateTime()) {
                response.put("status", 0);
                response.put("message", "Need Participate Time.");
            } else {
                MeetingJoinUser meetingJoinUser = new MeetingJoinUser(
                        meeting, user, needHotel, participate, false
                );
                meetingService.addJoinUserIntoMeeting(meetingJoinUser);
                response.put("status", 1);
                response.put("message", "Sign Up Success.");
            }

        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This Meeting isn't exist.");
        } catch (Exception e) {
            response.put("status", 0);
            response.put("message", "Sign Up Failed.");
        }


        return response;
    }

    /**
     * Get Meeting Api
     * @param id meeting id == this.
     * @param name meeting name contains this.
     * @param before meeting time before this.
     * @param after meeting time after this.
     * @param location meeting location contains this.
     * @param page the page number.
     * @return if have results return {
     *     "status": 1,
     *     "message": "Get Meetings Success.",
     *     "data": [
     *          {
     *              meeting data
     *          },
     *          ...
     *     ]
     * } else if don't have result return {
     *     "status": 0,
     *     "message": "This Meeting isn't exist.",
     * } else return {
     *     "status": 0,
     *     "message": "Get Meeting Failed.",
     * }
     */
    @ResponseBody
    @GetMapping("/get")
    public JSONObject get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Timestamp before,
            @RequestParam(required = false) Timestamp after,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        JSONObject response = new JSONObject();
        try {
            List<Meeting> list = meetingService.loadMeetings(id, name,
                    before, after, location, page);
            if (list.size() != 0) {
                response.put("status", 1);
                response.put("message", "Get Meetings Success.");
                response.put("data", list);
            } else {
                response.put("status", 0);
                response.put("message", "This Meeting isn't exist.");
            }
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "Get Meeting Failed.");
        }

        return response;
    }

}

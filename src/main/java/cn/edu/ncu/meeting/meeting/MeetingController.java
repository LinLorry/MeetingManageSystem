package cn.edu.ncu.meeting.meeting;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import cn.edu.ncu.meeting.meeting.model.MeetingJoinUser;
import cn.edu.ncu.meeting.util.QRCodeUtil;
import cn.edu.ncu.meeting.util.SecurityUtil;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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

    @Value("${Manage.Domain}")
    private String domain;


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
        User user = SecurityUtil.getUser();

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
     * Get Join Meeting User Api.
     * @param id the meeting id.
     * @return if get join user success return {
     *     "status": 1,
     *     "message": "Get meeting join users success.",
     *     "data": [
     *          {
     *              user data
     *          },
     *          ...
     *     ]
     * } else if return {
     *     "status": 0,
     *     "message": message: String
     * }
     */
    @GetMapping("/join")
    @ResponseBody
    public JSONObject getJoinUsers(@RequestParam long id) {
        JSONObject response = new JSONObject();

        Meeting meeting = meetingService.loadMeetingById(id);

        if (meeting.getHoldUser().getId() != SecurityUtil.getUserId() &&
                SecurityUtil.getAuthorities().isEmpty()) {
            response.put("status", 0);
            response.put("message", "You can't get join this meeting user info.");
            return response;
        }

        response.put("status", 1);
        response.put("message", "Get meeting join users success.");
        response.put("data", meeting.getJoinUserInfo());

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
        User user = SecurityUtil.getUser();
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
     * Cancel Join Meeting Api
     * @param id the meeting id.
     * @return {
     *     "status": 1,
     *     "message": "Cancel join meeting success."
     * }
     */
    @PostMapping("/cancelJoin")
    @ResponseBody
    public JSONObject cancelJoinMeeting(@RequestParam long id) {
        JSONObject response = new JSONObject();
        Meeting meeting = meetingService.loadMeetingById(id);

        MeetingJoinUser meetingJoinUser = new MeetingJoinUser();
        meetingJoinUser.setMeeting(meeting);
        meetingJoinUser.setUser(SecurityUtil.getUser());

        meetingService.removeJoinUserFromMeeting(meetingJoinUser);
        response.put("status", 1);
        response.put("message", "Cancel join meeting success.");

        return response;
    }

    /**
     * Get Meeting Join User Info Excel File Api.
     * @param id the meeting id.
     * @param response the response.
     */
    @GetMapping("/getJoinExcel")
    public void getJoinUsersExcel(@RequestParam long id, HttpServletResponse response) {
        Meeting meeting = meetingService.loadMeetingById(id);
        if (meeting.getHoldUser().getId() != SecurityUtil.getUserId() &&
                SecurityUtil.getAuthorities().isEmpty()) {
            JSONObject json = new JSONObject();
            json.put("status", 0);
            json.put("message", "You can't get join this meeting user info.");

            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            try {
                response.getOutputStream().print(json.toJSONString());
            } catch (Exception e) {
                logger.error(e);
            }

        } else {

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=meeting"+ meeting.getId() +"-joinUserInfo.xlsx"
            );
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            int row = 1, column = 0;

            Row header = sheet.createRow(0);
            Set<String> keySet = meeting.getJoinUserInfo().get(0).keySet();
            for (String key : keySet) {
                Cell cell = header.createCell(column++);
                cell.setCellValue(key);
            }

            column = 0;
            for (Map<String, Object> one : meeting.getJoinUserInfo()) {
                Row r = sheet.createRow(row++);
                for (String key : keySet) {
                    Object obj = one.get(key);
                    Cell cell = r.createCell(column++);

                    cell.setCellValue(obj == null ? "null" : obj.toString());

                }
                column = 0;
            }


            try {
                workbook.write(response.getOutputStream());
            } catch (Exception e) {
                logger.error(e);
            }
        }

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

    /**
     * Get Hold User Info Api.
     * @param id the meeting id.
     * @return If meeting exist return  {
     *     "status": 1,
     *     "message": "Get meeting hold user info success",
     *     "data": {
     *         "id": the user id: long,
     *         "username": the username: String
     *     }
     * } else return {
     *     "status": 0,
     *     "message": "This Meeting isn't exist."
     * }
     */
    @ResponseBody
    @GetMapping("/getHoldUser")
    public JSONObject getHoldUserInfo(@RequestParam long id) {
        JSONObject response = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            User user = meetingService.loadMeetingById(id).getHoldUser();
            data.put("id", user.getId());
            data.put("username", user.getName());

            response.put("status", 1);
            response.put("message", "Get meeting hold user info success");
            response.put("data", data);
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This Meeting isn't exist.");
        }
        return response;
    }

    /**
     * Get Hot Meeting Api.
     * @param page the page number.
     * @return if get success return {
     *     "status": 1,
     *     "message": "Get hot meeting success",
     *     "data": [
     *          meeting data...
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/getHot")
    public JSONObject getHot(@RequestParam(required = false, defaultValue = "0") int page) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get hot meeting success.");
        response.put("data", meetingService.loadHotMeeting(page));

        return response;
    }

    /**
     * Get Newest Meeting Api.
     * @param page the page number.
     * @return if get success return {
     *     "status": 1,
     *     "message": "Get newest meeting success.",
     *     "data": [
     *          meeting data...
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/getNewest")
    public JSONObject getNewest(@RequestParam(required = false, defaultValue = "0") int page) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get newest meeting success.");
        response.put("data", meetingService.loadNewestMeeting(page));

        return response;
    }

    /**
     * Get Start Soon Meeting Api.
     * @param page the page number.
     * @return if get success return {
     *     "status": 1,
     *     "message": "Get start soon meeting success.",
     *     "data": [
     *          meeting data...
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/getStartSoon")
    public JSONObject getStartSoon(@RequestParam(required = false, defaultValue = "0") int page) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get start soon meeting success.");
        response.put("data", meetingService.loadImmediatelyBeginMeeting(page));

        return response;
    }

    /**
     * Delete Meeting Api
     * @param request {
     *      "id": id: Integer not null
     * }
     * @return if delete success return {
     *     "status": 1,
     *     "message": "Delete meeting success."
     * } else if don't have authority to delete meeting return {
     *     "status": 0,
     *     "message": "You can't delete this meeting."
     * } else if meeting isn't exist return {
     *     "status": 0,
     *     "message": "This Meeting isn't exist."
     * } else return {
     *     "status": 0,
     *     "message": "Delete Meeting Failed."
     * }
     */
    @ResponseBody
    @PostMapping("/delete")
    public JSONObject delete(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        User user = SecurityUtil.getUser();

        try {
            Meeting meeting = meetingService.loadMeetingById(request.getLongValue("id"));

            if (meeting.getHoldUser().getId() != user.getId() && user.getAuthorities().isEmpty()) {
                response.put("status", 0);
                response.put("message", "You can't delete this meeting.");
            } else {
                meetingService.removeMeetingById(meeting.getId());
                response.put("status", 1);
                response.put("message", "Delete meeting success.");
            }
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This Meeting isn't exist.");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "Delete Meeting Failed.");
        }

        return response;
    }

    /**
     * Get Meeting QR Code Api.
     * @param id the meeting id.
     * @param response the response.
     */
    @GetMapping("/QRCode")
    public void getQRCode(@RequestParam long id, HttpServletResponse response) {
        String url = "http://" + domain + "/meeting.html?id=" + id;

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        try {
            BitMatrix QRCodeImg = QRCodeUtil.generateQRCodeStream(url);
            MatrixToImageWriter.writeToStream(QRCodeImg, "png", response.getOutputStream());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Have Join Meeting Api.
     * @param id the meeting id.
     * @return {
     *     "status": 1,
     *     "message": "Check join Success.",
     *     "data": have join: boolean
     * }
     */
    @ResponseBody
    @GetMapping("/haveJoin")
    public JSONObject haveJoin(@RequestParam long id) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Check join Success.");
        response.put("data", meetingService.checkUserJoinMeeting(id, SecurityUtil.getUserId()));

        return response;
    }
}

package cn.edu.ncu.meeting.meeting;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import cn.edu.ncu.meeting.meeting.model.MeetingJoinUser;
import cn.edu.ncu.meeting.meeting.repo.MeetingRepository;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.*;

/**
 * Meeting Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;

    private final SessionFactory sessionFactory;

    private static Specification<Meeting> equalId(long id) {
        return (meeting, cq, cb) -> cb.equal(meeting.get("id"), id);
    }

    private static Specification<Meeting> containsName(String name) {
        return (meeting, cq, cb) -> cb.like(meeting.get("name"), "%" + name + "%");
    }

    private static Specification<Meeting> containsLocation(String location) {
        return (meeting, cq, cb) -> cb.like(meeting.get("location"), "%" + location + "%");
    }

    private static Specification<Meeting> beforeTime(Timestamp time) {
        return (meeting, cq, cb) -> cb.lessThan(meeting.get("time"), time);
    }

    private static Specification<Meeting> afterTime(Timestamp time) {
        return (meeting, cq, cb) -> cb.greaterThan(meeting.get("time"), time);
    }

    public MeetingService(MeetingRepository meetingRepository, EntityManagerFactory factory) {
        this.meetingRepository = meetingRepository;
        if(factory.unwrap(SessionFactory.class) == null){
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    /**
     * Add Meeting
     * @param json have the meeting data json.
     * @param user the hold user.
     * @return the meeting have been created.
     */
    Meeting addMeeting(JSONObject json, User user) {
        Meeting meeting = new Meeting();

        meeting.setName(json.getString("name"));
        meeting.setTime(json.getTimestamp("time"));
        meeting.setLocation(json.getString("location"));
        meeting.setHotel(json.getString("hotel"));
        meeting.setComment(json.getString("comment"));
        meeting.setStar(json.getString("star"));
        meeting.setNeedName(json.getBooleanValue("needName"));
        meeting.setNeedOrganization(json.getBooleanValue("needOrganization"));
        meeting.setNeedIdCard(json.getBooleanValue("needIdCard"));
        meeting.setNeedParticipateTime(json.getBooleanValue("needParticipateTime"));
        meeting.setNeedGender(json.getBooleanValue("needGender"));
        meeting.setHoldUser(user);

        meetingRepository.save(meeting);
        return meeting;
    }

    /**
     * Add Meeting Join User
     * @param meetingJoinUser the Meeting Join User will be save.
     */
    void addJoinUserIntoMeeting(MeetingJoinUser meetingJoinUser) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.persist(meetingJoinUser);

        tx.commit();
        session.close();
    }

    /**
     * Remove Meeting Join User
     * @param meetingJoinUser the Meeting Join User will be delete.
     */
    void removeJoinUserFromMeeting(MeetingJoinUser meetingJoinUser) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.delete(session.merge(meetingJoinUser));

        tx.commit();
        session.close();
    }

    /**
     * get the Meeting by id
     * @param id the meeting id.
     * @return Meeting which id is param.
     * @throws NoSuchElementException if meeting doesn't exist, throw this exception.
     */
    Meeting loadMeetingById(long id) throws NoSuchElementException {
        return meetingRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Load Meetings by name, time, location
     * @param id meeting id == this.
     * @param name meeting name contains this.
     * @param before meeting time before this.
     * @param after meeting time after this.
     * @param location meeting location contains this.
     * @param page the page number.
     * @return the list of results.
     */
    List<Meeting> loadMeetings(Long id, String name, Timestamp before,
                               Timestamp after, String location, int page) {
        Specification<Meeting> s = null;

        if (id != null) {
            s = equalId(id);
        }

        if (name != null && name.length() != 0) {
            s = s == null ? containsName(name) : s.and(containsName(name));
        }

        if (before != null) {
            s = s == null ? beforeTime(before) : s.and(beforeTime(before));
        }

        if (after != null) {
            s = s == null ? afterTime(after) : s.and(afterTime(after));
        }

        if (location != null && location.length() != 0) {
            s = s == null ? containsLocation(location) : s.and(containsLocation(location));
        }

        if (s == null) {
            return new ArrayList<>();
        }

        return meetingRepository.findAll(s, PageRequest.of(page, 20)).getContent();
    }

    /**
     * Load Hot Meetings by page number
     * @param page the page number
     * @return the list of meetings.
     */
    List<Meeting> loadHotMeeting(int page) {
        return meetingRepository.findAllHot(PageRequest.of(page, 5)).getContent();
    }

    /**
     * Load Newest Meetings by page number
     * @param page the page number
     * @return the list of meetings.
     */
    List<Meeting> loadNewestMeeting(int page) {
        return meetingRepository.findAllByOrderByIdDesc(PageRequest.of(page, 5)).getContent();
    }

    /**
     * Load Immediately begin Meetings by page number
     * @param page the page number
     * @return the list of meetings.
     */
    List<Meeting> loadImmediatelyBeginMeeting(int page) {
        return meetingRepository.findAllImmediatelyBegin(PageRequest.of(page, 5)).getContent();
    }

    /**
     * Remove meeting by id
     * @param id the meeting which will be delete id.
     * @throws NoSuchElementException if meeting doesn't exist, throw this exception.
     */
    void removeMeetingById(long id) throws NoSuchElementException {
        meetingRepository.delete(
                meetingRepository.findById(id).orElseThrow(NoSuchElementException::new)
        );
    }

    /**
     * Check User Have Join Meeting.
     * @param meetingId the meeting id.
     * @param userId the user id.
     * @return if the user have join meeting return true, else return false.
     */
    boolean checkUserJoinMeeting(long meetingId, long userId) {
        return meetingRepository.existsByMeetingIdAndUserId(meetingId, userId);
    }
}

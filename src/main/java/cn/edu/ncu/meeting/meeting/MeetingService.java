package cn.edu.ncu.meeting.meeting;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import cn.edu.ncu.meeting.meeting.model.MeetingJoinUser;
import cn.edu.ncu.meeting.meeting.repo.MeetingRepository;
import cn.edu.ncu.meeting.user.model.User;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Meeting Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;

    private final SessionFactory sessionFactory;

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
        meeting.setNeedParticipateTime(json.getBooleanValue("needParticipate"));
        meeting.setNeedGender(json.getBooleanValue("needGender"));
        meeting.setHoldUser(user);

        meetingRepository.save(meeting);
        return meeting;
    }

    /**
     * Add Meeting Join User.
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
     * get the Meeting by id
     * @param id the meeting id.
     * @return Meeting which id is param.
     * @throws NoSuchElementException if meeting doesn't exist, throw this exception.
     */
    Meeting loadMeetingById(long id) throws NoSuchElementException {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isPresent()) {
            return optionalMeeting.get();
        }
        throw new NoSuchElementException("This Meeting isn't exist");
    }

}

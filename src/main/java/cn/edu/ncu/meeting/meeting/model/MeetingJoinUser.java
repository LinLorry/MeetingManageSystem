package cn.edu.ncu.meeting.meeting.model;

import cn.edu.ncu.meeting.user.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Meeting Join User
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class MeetingJoinUser implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn
    private Meeting meeting;

    @Id
    @ManyToOne
    @JoinColumn
    private User user;

    @Column(columnDefinition = "Boolean default false")
    private boolean needHotel;

    private Timestamp participateTime;

    @Column(columnDefinition = "Boolean default false")
    private boolean checkIn;

    public MeetingJoinUser(Meeting meeting, User user, boolean needHotel,
                           Timestamp participateTime, boolean checkIn) {
        this.meeting = meeting;
        this.user = user;
        this.needHotel = needHotel;
        this.participateTime = participateTime;
        this.checkIn = checkIn;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        System.out.println(meeting.getId());
        this.meeting = meeting;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        System.out.println(user.getId());

        this.user = user;
    }

    public boolean isNeedHotel() {
        return needHotel;
    }

    public void setNeedHotel(boolean needHotel) {
        this.needHotel = needHotel;
    }

    public Timestamp getParticipateTime() {
        return participateTime;
    }

    public void setParticipateTime(Timestamp participateTime) {
        this.participateTime = participateTime;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public void setCheckIn(boolean checkIn) {
        this.checkIn = checkIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MeetingJoinUser)) return false;
        MeetingJoinUser tmp = (MeetingJoinUser) obj;
        return tmp.user.equals(user) &&
                tmp.meeting.equals(meeting) &&
                tmp.participateTime.equals(participateTime) &&
                tmp.needHotel == needHotel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                user.getId(), meeting.getId(),
                participateTime, needHotel
        );
    }
}

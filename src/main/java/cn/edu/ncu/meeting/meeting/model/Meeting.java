package cn.edu.ncu.meeting.meeting.model;

import cn.edu.ncu.meeting.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 * The Meeting Model
 * @author lorry
 * @author lin864464995@163.com
 *
 */
@Entity
public class Meeting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Timestamp time;

    @Column(nullable = false)
    private String location;

    private String hotel;

    private String comment;

    private String star;

    @Column(columnDefinition = "Boolean default false")
    private boolean needName = false;

    @Column(columnDefinition = "Boolean default false")
    private boolean needOrganization = false;

    @Column(columnDefinition = "Boolean default false")
    private boolean needIdCard = false;

    @Column(columnDefinition = "Boolean default false")
    private boolean needParticipateTime = false;

    @Column(columnDefinition = "Boolean default false")
    private boolean needGender = false;

    @ManyToOne
    private User holdUser;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<MeetingJoinUser> joinUserSet = new HashSet<>();

    /**
     * Get Meeting Join User Info.
     * @return [
     *      {
     *          "needHotel": bool,
     *          "checkIn": bool,
     *          ["name": String: if needName is true,]
     *          ["gender": bool: if needGender is true,]
     *          ["participateTime": Timestamp: if needParticipateTime is true,]
     *          ["idCard": String: if needIdCard is true,]
     *          ["organization": if needOrganization is true]
     *      },
     *      ...
     * ]
     */
    public List<Map<String, Object>> getJoinUserInfo() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (MeetingJoinUser meetingJoinUser : joinUserSet) {
            User user = meetingJoinUser.getUser();
            Map<String, Object> map = new HashMap<>();

            map.put("needHotel", meetingJoinUser.isNeedHotel());
            map.put("checkIn", meetingJoinUser.isCheckIn());

            if (needName) {
                map.put("name", user.getName());
            }

            if (needGender) {
                map.put("gender", user.isGender());
            }

            if (needParticipateTime) {
                map.put("participateTime", meetingJoinUser.getParticipateTime());
            }

            if (needIdCard) {
                map.put("idCard", user.getIdCard());
            }

            if (needOrganization) {
                map.put("organization", user.getOrganization());
            }

            list.add(map);
        }

        return list;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public boolean isNeedName() {
        return needName;
    }

    public void setNeedName(boolean needName) {
        this.needName = needName;
    }

    public boolean isNeedOrganization() {
        return needOrganization;
    }

    public void setNeedOrganization(boolean needOrganization) {
        this.needOrganization = needOrganization;
    }

    public boolean isNeedIdCard() {
        return needIdCard;
    }

    public void setNeedIdCard(boolean needIdCard) {
        this.needIdCard = needIdCard;
    }

    public boolean isNeedParticipateTime() {
        return needParticipateTime;
    }

    public void setNeedParticipateTime(boolean needParticipateTime) {
        this.needParticipateTime = needParticipateTime;
    }

    public boolean isNeedGender() {
        return needGender;
    }

    public void setNeedGender(boolean needGender) {
        this.needGender = needGender;
    }

    public User getHoldUser() {
        return holdUser;
    }

    public void setHoldUser(User holdUser) {
        this.holdUser = holdUser;
    }

    public Set<MeetingJoinUser> getJoinUserSet() {
        return joinUserSet;
    }

    public void setJoinUserSet(Set<MeetingJoinUser> joinUserSet) {
        this.joinUserSet = joinUserSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof Meeting)) return false;
        Meeting tmp = (Meeting) obj;

        return tmp.getId() == id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

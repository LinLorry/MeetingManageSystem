package cn.edu.ncu.meeting.user.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserKey implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name = "username")
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o==this) return true;
        if (!(o instanceof UserKey)) return false;
        UserKey tmp = (UserKey) o;
        return tmp.id.equals(id) && tmp.username.equals(username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}

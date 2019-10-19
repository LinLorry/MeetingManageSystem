package cn.edu.ncu.meeting.meeting.repo;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import org.springframework.data.repository.CrudRepository;

/**
 * Meeting Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
public interface MeetingRepository extends CrudRepository<Meeting, Long> {
}

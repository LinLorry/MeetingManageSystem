package cn.edu.ncu.meeting.meeting.repo;

import cn.edu.ncu.meeting.meeting.model.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Meeting Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 */
public interface MeetingRepository extends CrudRepository<Meeting, Long>, JpaSpecificationExecutor<Meeting> {
    /**
     * Get the most join user meeting which not over time.
     * @param pageable the page number
     * @return Meeting Page.
     */
    @Query(value = "SELECT m " +
            "FROM Meeting m " +
            "LEFT JOIN MeetingJoinUser mju on mju.meeting.id = m.id " +
            "where m.time > current_time " +
            "group by m.id " +
            "order by count(mju) desc")
    Page<Meeting> findAllHot(Pageable pageable);

    Page<Meeting> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Get not only Immediate Begin but also not over time Meetings
     * @param pageable the page number.
     * @return Meeting Page.
     */
    @Query(value = "FROM Meeting m WHERE m.time > current_time ORDER BY m.time asc")
    Page<Meeting> findAllImmediatelyBegin(Pageable pageable);

    /**
     * Exists By Meeting id and User id
     * @param meetingId the meeting id.
     * @param UserId the user id.
     * @return If is exists return true else false.
     */
    @Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM MeetingJoinUser r WHERE r.meeting.id = ?1 AND r.user.id = ?2")
    boolean existsByMeetingIdAndUserId(Long meetingId, Long UserId);
}

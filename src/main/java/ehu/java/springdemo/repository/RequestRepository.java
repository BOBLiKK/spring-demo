package ehu.java.springdemo.repository;

import ehu.java.springdemo.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);

    @Query("SELECT r.id, r.comment, r.dateOfBirth, r.firstName, r.lastName, r.nationality, r.reasonForWanted, r.reward, r.status, r.userId FROM Request r WHERE r.userId = :userId")
    List<Object[]> findRequestSummaryByUserId(@Param("userId") Long userId);
}

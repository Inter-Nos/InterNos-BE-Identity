package app.internos.servicea.domain.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface VisitLogUserRepository extends JpaRepository<VisitLogUser, Long> {
    
    List<VisitLogUser> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    @Query("SELECT v FROM VisitLogUser v WHERE v.owner.id = :ownerId AND v.createdAt >= :since ORDER BY v.createdAt DESC")
    List<VisitLogUser> findByOwnerIdAndCreatedAtAfter(
            @Param("ownerId") Long ownerId,
            @Param("since") Instant since
    );
    
    @Query("SELECT COUNT(v) FROM VisitLogUser v WHERE v.owner.id = :ownerId AND v.createdAt >= :since")
    long countByOwnerIdAndCreatedAtAfter(
            @Param("ownerId") Long ownerId,
            @Param("since") Instant since
    );
}


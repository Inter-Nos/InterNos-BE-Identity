package app.internos.servicea.domain.visit;

import app.internos.servicea.domain.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "visit_log_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitLogUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;
    
    @Column(name = "visitor_anon_id")
    private String visitorAnonId;
    
    @Column(name = "ip_hash", nullable = false)
    private String ipHash;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}


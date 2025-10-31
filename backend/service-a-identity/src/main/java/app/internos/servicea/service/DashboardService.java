package app.internos.servicea.service;

import app.internos.servicea.domain.visit.VisitLogUserRepository;
import app.internos.servicea.dto.response.DashboardResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final VisitLogUserRepository visitLogUserRepository;
    // TODO: Service B FeignClient for rooms summary (to be implemented later)
    
    /**
     * Get dashboard data for the user
     * Aggregates profile visits from Service A and prepares rooms summary from Service B
     */
    @Transactional(readOnly = true)
    public DashboardResp getDashboard(Long userId, String range) {
        // Parse range (24h, 7d, all)
        Instant since = parseRange(range);
        
        // Get profile visits
        DashboardResp.ProfileVisits profileVisits = getProfileVisits(userId, since, range);
        
        // TODO: Get rooms summary from Service B via FeignClient
        // For now, return empty summary
        DashboardResp.RoomsSummary roomsSummary = DashboardResp.RoomsSummary.builder()
                .total(0)
                .pub(0)
                .priv(0)
                .visits(0)
                .attempts(0)
                .solved(0)
                .solveRate(0.0)
                .build();
        
        return DashboardResp.builder()
                .profileVisits(profileVisits)
                .roomsSummary(roomsSummary)
                .build();
    }
    
    private DashboardResp.ProfileVisits getProfileVisits(Long userId, Instant since, String range) {
        long count = visitLogUserRepository.countByOwnerIdAndCreatedAtAfter(userId, since);
        
        // Generate sparkline data (last 24 hours, hourly buckets)
        List<Integer> sparkline = generateSparkline(userId, since);
        
        return DashboardResp.ProfileVisits.builder()
                .count((int) count)
                .range(range)
                .sparkline(sparkline)
                .build();
    }
    
    private List<Integer> generateSparkline(Long userId, Instant since) {
        List<Integer> sparkline = new ArrayList<>();
        Instant now = Instant.now();
        
        // Generate 24 hourly buckets
        for (int i = 23; i >= 0; i--) {
            Instant bucketStart = now.minusSeconds(i * 3600);
            Instant bucketEnd = bucketStart.plusSeconds(3600);
            
            if (bucketStart.isBefore(since)) {
                sparkline.add(0);
            } else {
                long count = visitLogUserRepository.countByOwnerIdAndCreatedAtAfter(userId, bucketStart);
                // TODO: Count visits between bucketStart and bucketEnd (requires query modification)
                // For now, return aggregated count
                sparkline.add((int) count);
            }
        }
        
        return sparkline;
    }
    
    private Instant parseRange(String range) {
        Instant now = Instant.now();
        
        if (range == null || "24h".equals(range)) {
            return now.minus(Duration.ofHours(24));
        } else if ("7d".equals(range)) {
            return now.minus(Duration.ofDays(7));
        } else if ("all".equals(range)) {
            return Instant.EPOCH; // Beginning of time
        } else {
            return now.minus(Duration.ofHours(24)); // Default to 24h
        }
    }
}


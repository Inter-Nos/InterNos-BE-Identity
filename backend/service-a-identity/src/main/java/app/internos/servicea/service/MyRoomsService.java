package app.internos.servicea.service;

import app.internos.servicea.dto.response.MyRoomItem;
import app.internos.servicea.dto.response.MyRoomsResp;
import app.internos.servicea.dto.response.RoomMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyRoomsService {
    
    // TODO: Service B FeignClient for rooms data (to be implemented when Service B is ready)
    // private final ServiceBClient serviceBClient;
    
    /**
     * Get list of my rooms with per-room metrics
     * 
     * @param ownerId Current user ID
     * @param limit Maximum number of rooms to return
     * @param offset Number of rooms to skip
     * @return MyRoomsResp with room list and metrics
     */
    @Transactional(readOnly = true)
    public MyRoomsResp getMyRooms(Long ownerId, Integer limit, Integer offset) {
        // TODO: Call Service B to get rooms owned by this user
        // For now, return empty list until Service B is implemented
        // Example:
        // List<RoomMeta> rooms = serviceBClient.getRoomsByOwnerId(ownerId, limit, offset);
        // int total = serviceBClient.countRoomsByOwnerId(ownerId);
        
        List<MyRoomItem> items = new ArrayList<>();
        int total = 0;
        
        // TODO: For each room, calculate metrics:
        // 1. Get visits from Service A tracking data (profile visits + room visits)
        // 2. Get attempts/solved from Service B solve records
        // 3. Calculate solveRate = solved / attempts (handle division by zero)
        
        // Placeholder until Service B is implemented
        log.info("Getting rooms for ownerId: {}, limit: {}, offset: {}", ownerId, limit, offset);
        
        boolean hasMore = total > (offset + limit);
        
        return MyRoomsResp.builder()
                .items(items)
                .total(total)
                .hasMore(hasMore)
                .build();
    }
    
    /**
     * Calculate metrics for a room
     * Combines data from Service A (visits) and Service B (attempts, solved)
     * 
     * @param roomId Room ID
     * @param ownerId Owner ID (for profile visits)
     * @return RoomMetrics
     */
    private RoomMetrics calculateRoomMetrics(Long roomId, Long ownerId) {
        // TODO: Implement metrics calculation
        // 1. Get profile visits from Service A (VisitLogUser)
        // 2. Get room visits from Service B (VisitLogRoom)
        // 3. Get solve attempts and successes from Service B (Attempt)
        // 4. Calculate solveRate
        
        // Placeholder
        return RoomMetrics.builder()
                .visits(0)
                .attempts(0)
                .solved(0)
                .solveRate(0.0)
                .build();
    }
}


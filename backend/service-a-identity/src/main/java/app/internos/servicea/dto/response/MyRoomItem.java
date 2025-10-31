package app.internos.servicea.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyRoomItem {
    
    // Room 기본 정보 (Service B의 RoomMeta와 호환)
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String title;
    private String hint;
    private String visibility; // PUBLIC, PRIVATE
    private String policy; // ONCE, LIMITED, UNLIMITED
    private Integer viewLimit;
    private Integer viewsUsed;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant expiresAt;
    
    private Boolean isActive;
    private String contentType; // TEXT, IMAGE
    private String thumbnailUrl;
    
    // 추가: 방별 통계 메트릭
    private RoomMetrics metrics;
}


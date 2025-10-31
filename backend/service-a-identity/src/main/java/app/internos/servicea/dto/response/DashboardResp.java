package app.internos.servicea.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResp {
    
    private ProfileVisits profileVisits;
    
    private RoomsSummary roomsSummary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileVisits {
        private Integer count;
        private String range;
        private List<Integer> sparkline;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomsSummary {
        private Integer total;
        private Integer pub;
        private Integer priv;
        private Integer visits;
        private Integer attempts;
        private Integer solved;
        private Double solveRate;
    }
}


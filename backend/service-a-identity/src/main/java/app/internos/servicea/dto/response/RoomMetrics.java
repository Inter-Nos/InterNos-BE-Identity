package app.internos.servicea.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMetrics {
    
    private Integer visits;
    private Integer attempts;
    private Integer solved;
    private Double solveRate;
}


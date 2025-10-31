package app.internos.servicea.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackUserVisitReq {
    
    @NotNull(message = "ownerId is required")
    private Long ownerId;
    
    private String visitorAnonId;
    
    private String ua;
}


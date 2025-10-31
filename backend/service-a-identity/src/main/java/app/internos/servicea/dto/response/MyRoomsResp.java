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
public class MyRoomsResp {
    
    private List<MyRoomItem> items;
    private Integer total;
    private Boolean hasMore;
}


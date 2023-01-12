package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.model.Bill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    private Bill bill;
    private OrderItemResponseDTO orderItemResponseDTO;
}

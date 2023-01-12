package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.CustomerDto;
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table khach hang")
public class DetailsItemOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String type; // giá sản phẩm theo loại bán - loại nhập
    private String dvtCode;
    private Integer quality;
    private Double totalPrice; // gia tien theo don vi tinh * quality
    private Long idItems;
    private Long idOrder; // đơn hàng nào

    public static DetailsItemOrder of(DetailsItemOrderDto detailsItemOrderDto, Long cid, String uid){
        DetailsItemOrder detailsItemOrder = DetailsItemOrder.builder()
                .dvtCode(detailsItemOrderDto.getDvtCode())
                .quality(detailsItemOrderDto.getQuality())
                .totalPrice(detailsItemOrderDto.getTotalPrice())
                .idOrder(detailsItemOrderDto.getIdOrder())
                .idItems(detailsItemOrderDto.getIdItems()).build();
        detailsItemOrder.setCreateBy(uid);
        detailsItemOrder.setCompanyId(cid);
        return detailsItemOrder;
    }
}

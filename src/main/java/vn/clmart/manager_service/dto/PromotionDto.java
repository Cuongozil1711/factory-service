package vn.clmart.manager_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PromotionDto {
    private String code; // mã khuyến mãi
    private String name; // tên khuyến mãi
    private Integer percent; // phần trăm giảm
    private Double price; // giá giảm
    private Long idItems; // san pham phẩm
    private Integer deleteFlg; // trạng thái
    private Date startDate; // ngày hết hiệu lực
    private Date endDate; // ngày kết thúc hiệu lực
    private String type; // loại mức giảm
    private String typePromotion; // loại khuyến mại
    private String typeCondition; // loại điều kiện đơn tối thiệu
    private Long totalPrice; // giá theo theo điều kiên đơn
    private Integer totalQuantity; // tổng số lượng theo điều kiện đơn
    private Long idItemsDonate; // sản phảm đính kèm
    private Integer quantity; // số lượng
}

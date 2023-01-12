package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.CategoryDto;
import vn.clmart.manager_service.model.config.ListHashMapConverter;
import vn.clmart.manager_service.model.config.ListHashMapConverterString;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table loai mat hang")
public class Category extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    @Convert(converter = ListHashMapConverterString.class)
    @Column(columnDefinition = "text")
    private List<Map<String, String>> unit;// đơn vị tính theo loại sản phẩm

    public static Category of(CategoryDto categoryDto, Long cid, String uid){
        Category category = Category.builder()
                .code(categoryDto.getCode())
                .name(categoryDto.getName())
                .build();
        category.setUnit(categoryDto.getUnit());
        category.setCreateBy(uid);
        category.setCompanyId(cid);
        return category;
    }
}

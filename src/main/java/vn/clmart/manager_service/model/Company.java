package vn.clmart.manager_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import vn.clmart.manager_service.dto.CompanyDto;
import vn.clmart.manager_service.dto.CustomerDto;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table cong ty")
public class Company {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;

    public static Company of(CompanyDto companyDto, Long cid, String uid){
        Company company = Company.builder()
                .code(companyDto.getCode())
                .name(companyDto.getName()).build();
        return company;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    private Date modifiedDate;
    private Integer deleteFlg;
    @CreatedBy
    private String createBy;
    @LastModifiedBy
    private String updateBy;
    @Transient
    private boolean isNew;
    @JsonIgnore
    public boolean isNew() {
        return this.isNew;
    }

    @PrePersist
    void setInitialDate() {
        this.createDate = this.modifiedDate = new Date();
        if (this.createBy == null) {
            this.createBy = "system";
        }

        if (this.updateBy == null) {
            this.updateBy = "system";
        }

        if (this.deleteFlg == null) {
            this.deleteFlg = 1;
        }

    }

    @PreUpdate
    void updateDate() {
        this.modifiedDate = new Date();
    }

    public Date getCreateDate() {
        return createDate;
    }

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssZ",
            timezone = "Asia/Ho_Chi_Minh"
    )
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(Integer deleteFlg) {
        this.deleteFlg = deleteFlg;
    }


    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }


    @Override
    public String toString() {
        return "PersistableEntity{" +
                "createDate=" + createDate +
                ", modifiedDate=" + modifiedDate +
                ", deleteFlg=" + deleteFlg +
                ", createBy='" + createBy + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", isNew=" + isNew +
                '}';
    }
}

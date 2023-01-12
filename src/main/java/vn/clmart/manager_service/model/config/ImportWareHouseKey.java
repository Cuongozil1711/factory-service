package vn.clmart.manager_service.model.config;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


public class ImportWareHouseKey implements Serializable {
    private Long id;
    private Long companyIdWork;

    public ImportWareHouseKey(Long id, Long companyIdWork) {
        this.id = id;
        this.companyIdWork = companyIdWork;
    }

    public ImportWareHouseKey() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyIdWork() {
        return companyIdWork;
    }

    public void setCompanyIdWork(Long companyIdWork) {
        this.companyIdWork = companyIdWork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportWareHouseKey that = (ImportWareHouseKey) o;
        return Objects.equals(id, that.id) && Objects.equals(companyIdWork, that.companyIdWork);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyIdWork);
    }
}

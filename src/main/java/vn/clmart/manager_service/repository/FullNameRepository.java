package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.FullName;

public interface FullNameRepository extends JpaRepository<FullName, Long> {
    FullName findByCompanyIdAndId(Long cid, Long id);
}

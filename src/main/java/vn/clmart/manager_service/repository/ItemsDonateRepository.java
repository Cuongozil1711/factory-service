package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Condition;
import vn.clmart.manager_service.model.ItemsDonate;

import java.util.Optional;

public interface ItemsDonateRepository extends JpaRepository<ItemsDonate, Long> {
    Optional<ItemsDonate> findByIdAndCompanyId(Long id, Long cid);
}

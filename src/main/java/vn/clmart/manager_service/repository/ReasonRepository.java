package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Reason;

import java.util.Optional;

public interface ReasonRepository extends JpaRepository<Reason, Long> {
    Optional<Reason> findByIdAndDeleteFlg(Long id, Integer deleteFlg);

    Page<Reason> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);
}

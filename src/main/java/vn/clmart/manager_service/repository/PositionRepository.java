package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.PageableExecutionUtils;
import vn.clmart.manager_service.model.Position;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Optional<Position> findByIdAndDeleteFlg(Long id, Integer deleteFlg);
    Optional<Position> findByCodeAndDeleteFlg(String code, Integer deleteFlg);

    Page<Position> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);

}

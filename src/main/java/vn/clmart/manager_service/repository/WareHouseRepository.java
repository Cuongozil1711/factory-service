package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Stalls;
import vn.clmart.manager_service.model.WareHouse;

import java.util.Optional;

public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {
    Optional<WareHouse> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Page<WareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);
    Page<WareHouse> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);
}

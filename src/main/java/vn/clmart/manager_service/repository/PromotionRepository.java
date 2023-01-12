package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.Promotion;
import java.util.*;
import java.util.Optional;

public interface PromotionRepository  extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);

    Optional<Promotion> findByIdAndCompanyId(Long id, Long cid);

    Page<Promotion> findAllByCompanyIdAndDeleteFlg(Long cid, Integer deleteFlg, Pageable pageable);

    Page<Promotion> findAllByCompanyId(Long cid, Pageable pageable);

    @Query("select p from Promotion as p where p.companyId = :cid and p.deleteFlg = :deleteFlg and p.idItems = :idItems and current_date  between p.dateFrom and p.dateEnd")
    List<Promotion> getByItems(Long cid, Integer deleteFlg, Long idItems);
}

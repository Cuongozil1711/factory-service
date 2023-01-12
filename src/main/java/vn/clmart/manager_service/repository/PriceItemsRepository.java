package vn.clmart.manager_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.PriceItems;
import java.util.*;
public interface PriceItemsRepository extends JpaRepository<PriceItems, Long> {

    List<PriceItems> findAllByIdItemsAndDeleteFlg(Long idItems, Integer deleteFlg);

    Optional<PriceItems> findByIdItemsAndDeleteFlgAndDvtCode(Long idItems, Integer deleteFlg, String dvtCode);

    List<PriceItems> findAllByIdItemsAndDeleteFlgAndQuality(Long idItems, Integer deleteFlg, Integer quality);

}

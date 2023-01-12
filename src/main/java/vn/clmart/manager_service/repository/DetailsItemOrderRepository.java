package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.DetailsItemOrder;
import java.util.*;
public interface DetailsItemOrderRepository  extends JpaRepository<DetailsItemOrder, Long> {
    List<DetailsItemOrder> findAllByCompanyIdAndDeleteFlgAndIdOrder(Long cid, Integer deleteFlg, Long idTems);

    List<DetailsItemOrder> findAllByCompanyIdAndIdOrder(Long cid, Long idTems);
}

package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.clmart.manager_service.model.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndDeleteFlg(Long cid, Integer deleteFlg);

    Page<Customer> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);

    List<Customer> findAllByDeleteFlg(Integer deleteFlg);

    @Query(value = "select count(*) from `customer` as o where date_format(o.create_date,'%Y, %m') = date_format(now(),'%Y, %m') and " +
            " o.delete_flg = 1 ", nativeQuery = true)
    Integer getCount();
}

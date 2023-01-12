package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Company;
import java.util.*;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByIdAndDeleteFlg(Long id, Integer deleteFlg);

    Page<Company> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);

    List<Company> findAllByDeleteFlg(Integer deleteFlg);
}

package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Category;
import vn.clmart.manager_service.model.Items;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndDeleteFlg(Long id, Integer deleteFlg);


    Page<Category> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);

}

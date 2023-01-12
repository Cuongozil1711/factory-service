package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Publisher;
import vn.clmart.manager_service.model.Stalls;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByIdAndDeleteFlg(Long id, Integer deleteFlg);

    Page<Publisher> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable);
}

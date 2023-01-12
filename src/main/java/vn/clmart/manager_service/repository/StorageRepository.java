package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Storage;

public interface StorageRepository extends JpaRepository<Storage, Long> {
}

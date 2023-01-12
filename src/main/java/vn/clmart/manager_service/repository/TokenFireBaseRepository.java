package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.TokenFireBase;

import java.util.Optional;

public interface TokenFireBaseRepository extends JpaRepository<TokenFireBase, Long> {
    Optional<TokenFireBase> findByDeleteFlgAndUserId(Integer status, String uid);
}

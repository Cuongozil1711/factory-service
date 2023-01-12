package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Address;
import vn.clmart.manager_service.model.FullName;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByCompanyIdAndId(Long cid, Long id);
}

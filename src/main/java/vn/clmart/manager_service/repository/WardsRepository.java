package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Wards;

import java.util.List;

public interface WardsRepository extends JpaRepository<Wards, Integer> {
    List<Wards> findAllByDistrictId(Integer districtId);
}

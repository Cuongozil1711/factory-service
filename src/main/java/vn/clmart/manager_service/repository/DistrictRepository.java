package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.District;
import java.util.*;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findAllByProvinceId(Integer provinceId);
}

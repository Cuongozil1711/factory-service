package vn.clmart.manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.clmart.manager_service.model.Province;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {

}

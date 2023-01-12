package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.model.Items;
import vn.clmart.manager_service.model.Position;

import java.util.*;

public interface ItemsRepository extends JpaRepository<Items, Long> {

    Optional<Items> findByIdAndDeleteFlg(Long id, Integer deleteFlg);


    @Query("select i from Items as i where  i.deleteFlg = :deleteFlg and " +
            "((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) " +
            "like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            "and (coalesce(:idCategory, -1) = i.idCategory or coalesce(:idCategory, -1) = -1) " +
            "and (coalesce(:idPubliser, -1) = i.idPubliser or coalesce(:idPubliser, -1) = -1) " +
            "and (coalesce(:idStall, -1) = i.idStall or coalesce(:idStall, -1) = -1) ")
    Page<Items> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable, Long idCategory, Long idPubliser, Long idStall,String search);


    @Query("select i from Items as i where " +
            "((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) " +
            "like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            "and (coalesce(:idCategory, -1) = i.idCategory or coalesce(:idCategory, -1) = -1) " +
            "and (coalesce(:idPubliser, -1) = i.idPubliser or coalesce(:idPubliser, -1) = -1) " +
            "and (coalesce(:idStall, -1) = i.idStall or coalesce(:idStall, -1) = -1) ")
    Page<Items> findAllItems(Pageable pageable, Long idCategory, Long idPubliser, Long idStall,String search);


    @Query("select i from Items as i where  i.deleteFlg = :deleteFlg and ((lower(concat(coalesce(i.code, ''), coalesce(i.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) ")
    Page<Items> findAllByDeleteFlg(Integer deleteFlg, Pageable pageable, String search);


    List<Items> findAllByDeleteFlg(Integer deleteFlg);
}

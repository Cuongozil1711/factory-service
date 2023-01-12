package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;

import java.util.Optional;

public interface ReceiptExportWareHouseRepository extends JpaRepository<ReceiptExportWareHouse, Long> {
    Optional<ReceiptExportWareHouse> findByIdAndCompanyIdAndDeleteFlg(Long id, Long cid, Integer deleteFlg);
    Optional<ReceiptExportWareHouse> findByIdAndDeleteFlg(Long id, Integer deleteFlg);
    Optional<ReceiptExportWareHouse> findByCodeAndDeleteFlg(String code, Integer deleteFlg);

    Page<ReceiptExportWareHouse> findAllByCompanyIdAndDeleteFlgOrderByCreateDateDesc(Long cid, Integer deleteFlg, Pageable pageable);

    @Query(value = "select count(case when month(e.create_date) = :month and year(e.create_date) =:year then 0 end) as t1  " +
            " from  `receipt_export_ware_house` as e " +
            "where e.company_id = :cid and e.delete_flg = :deleteFlg and e.state = 'COMPLETE'", nativeQuery = true)
    Integer getImportForMonth(@Param("cid") Long cid, Integer deleteFlg, Integer month, Integer year);

    @Query(value = "select count(*) from `receipt_export_ware_house` as o where " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg", nativeQuery = true)
    Integer getCountExport(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg
    );
}

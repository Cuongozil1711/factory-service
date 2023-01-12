package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.Order;

import java.util.*;

public interface ExportWareHouseRepository extends JpaRepository<ExportWareHouse, Long> {

    public List<ExportWareHouse> findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Integer delete, Long idReceiptExport, Long cid);

    public List<ExportWareHouse> findAllByIdReceiptExportAndCompanyId(Long idReceiptExport, Long cid);

    public List<ExportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyId(Integer delete, Long idItems, Long cid);

    @Query("select i from ExportWareHouse as i inner join ReceiptExportWareHouse as r on i.idReceiptExport = r.id " +
            "where i.companyId = :cid and i.deleteFlg = :delete " +
            "and ((lower(concat(coalesce(r.code, ''), coalesce(r.name, ''))) " +
            "like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '')) " +
            "and i.idReceiptExport is not null " +
            "and (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date)) " +
            "group by i.idReceiptExport order by i.createDate desc")
    public Page<ExportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, String search, Date startDate, Date endDate ,Pageable pageable);

    @Query("select i from ExportWareHouse as i where i.companyId = :cid " +
            " and i.deleteFlg = :delete " +
            " and  ((coalesce(i.createDate,current_date) between coalesce(:startDate, current_date) and coalesce(:endDate, current_date))) " +
            " and ((lower(concat(coalesce(i.code, ''),'')) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            " and (i.idReceiptExport is not null or  i.idOrder is not null) " +
            " group by i.idReceiptExport, i.idOrder " +
            " order by i.createDate desc ")
    public Page<ExportWareHouse> statisticalByCompanyIdAndDeleteFlgAndOrder(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);

    public List<ExportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Integer deleteFlg, Long idItems, Long cid, Long idReceiptImport);

    public List<ExportWareHouse> findAllByDeleteFlgAndCompanyIdAndIdReceiptImport(Integer deleteFlg,Long cid, Long idReceiptImport);

    public List<ExportWareHouse> findAllByCompanyIdAndDeleteFlgAndIdOrder(Long cid, Integer deleteFlg, Long idOrder);

    public List<ExportWareHouse> findAllByCompanyIdAndIdOrderAndIdItemsAndDvtCode(Long cid, Long idOrder, Long idItems, String dvtCode);

    @Query("select i from ExportWareHouse as i where i.companyId = :cid " +
            " and i.deleteFlg = :delete " +
            " and  ((coalesce(i.createDate,current_date) between coalesce(:startDate, current_date) and coalesce(:endDate, current_date)) " +
            " or coalesce(:startDate, current_date) = coalesce(:endDate, current_date)) " +
            " and ((lower(concat(coalesce(i.code, ''),'')) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            " order by i.createDate desc ")
    public Page<ExportWareHouse> listExport(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);



    @Query(value = "select * from export_ware_house as o where o.delete_flg = :delete " +
            "and o.company_id = :cid and date_format(o.create_date,'%Y, %m') = date_format(now(),'%Y, %m')" +
            " and o.id_receipt_export is null  group by o.id_items limit 3", nativeQuery = true)
    List<ExportWareHouse> getListOrder(Long cid, Integer delete);

    @Query(value = "select count(*) from `export_ware_house` as o where date_format(o.create_date,'%Y-%m-%d') = date_format(:date,'%Y-%m-%d') and " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg and o.id_order is null", nativeQuery = true)
    Integer getCountByDate(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg,
            @Param("date")
                    Date date
    );

    @Query(value = "select count(*) from `export_ware_house` as o where " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg", nativeQuery = true)
    Integer getCountExport(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg
    );

    @Query("select i from ExportWareHouse i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.deleteFlg = :deleteFlg" +
            " and i.idItems = :idItems and i.companyId = :cid and (r.idWareHouse = :idWareHouse or coalesce(:idWareHouse, -1) = -1)")
    public List<ExportWareHouse> getAllByIdWareHouse(Integer deleteFlg, Long idItems, Long cid, Long idWareHouse);
}

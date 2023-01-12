package vn.clmart.manager_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.clmart.manager_service.model.ImportWareHouse;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ImportWareHouseRepository extends JpaRepository<ImportWareHouse, Long> {

    public List<ImportWareHouse> findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Integer delete, Long idReceiptImport, Long cid);

    public List<ImportWareHouse> findAllByIdReceiptImportAndCompanyIdWork(Long idReceiptImport, Long cid);
    public List<ImportWareHouse> findAllByIdReceiptImportAndCompanyIdWorkAndIdItems(Long idReceiptImport, Long cid, Long idItems);


    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdWork(Integer delete, Long idItems, Long cid);

    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdWorkOrderByDateExpiredAsc(Integer delete, Long idItems, Long cid);

    public Optional<ImportWareHouse> findByCompanyIdWorkAndIdAndDeleteFlg(Long cid, Long id, Integer deteteFlg);
    public Optional<ImportWareHouse> findByCompanyIdWorkAndIdReceiptImportAndIdItemsAndDeleteFlg(Long cid, Long idReceiptImport, Long idItems, Integer deteteFlg);


    @Query("select i from ImportWareHouse as i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.companyIdWork = :cid and i.deleteFlg = :delete " +
            "and  (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date) " +
            "or coalesce(:startDate, current_date) = coalesce(:endDate, current_date) ) " +
            "and ((lower(concat(coalesce(r.code, ''), coalesce(r.name, ''))) " +
            "like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) " +
            "group by i.idReceiptImport order by i.createDate desc")
    public Page<ImportWareHouse> findAllByCompanyIdAndDeleteFlg(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);

    @Query("select i from ImportWareHouse as i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.companyIdWork = :cid and   (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date) or coalesce(:startDate, current_date) = coalesce(:endDate, current_date) ) and ((lower(concat(coalesce(r.code, ''), coalesce(r.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) group by i.idReceiptImport order by i.createDate desc ")
    public Page<ImportWareHouse> findAllByCompanyId(Long cid, String search, Date startDate, Date endDate, Pageable pageable);


    @Query("select i from ImportWareHouse as i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.companyIdWork = :cid and i.deleteFlg = :delete and  (i.createDate between coalesce(:startDate, current_date) and coalesce(:endDate, current_date) or coalesce(:startDate, current_date) = coalesce(:endDate, current_date) ) and ((lower(concat(coalesce(r.code, ''), coalesce(r.name, ''))) like lower(concat('%',coalesce(:search, ''), '%')))  or (coalesce(:search, '') = '') ) order by i.createDate desc ")
    public Page<ImportWareHouse> listImportWareHouse(Long cid, Integer delete, String search, Date startDate, Date endDate, Pageable pageable);


    Optional<ImportWareHouse> findAllByCompanyIdWorkAndId(Long cid, Long id);

    @Query(value = "select count(*) from `import_ware_house` as o where date_format(o.create_date,'%Y-%m-%d') = date_format(:date,'%Y-%m-%d') and " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg", nativeQuery = true)
    Integer getCountByDate(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg,
            @Param("date")
                    Date date
    );

    @Query(value = "select count(*) from `import_ware_house` as o where " +
            "o.company_id = :cid and o.delete_flg = :deleteFlg", nativeQuery = true)
    Integer getCountImport(
            @Param("cid")
                    Long cid,
            @Param("deleteFlg")
                    Integer deleteFlg
    );

    @Query("select i from ImportWareHouse i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.deleteFlg = :delete" +
            " and i.idItems = :idItems and i.companyIdWork = :cid  and (r.idWareHouse = :idWareHouse or coalesce(:idWareHouse, -1) = -1)")
    public List<ImportWareHouse> getImportWareHouse(Integer delete, Long idItems, Long cid, Long idWareHouse);


    @Query("select i from ImportWareHouse i inner join ReceiptImportWareHouse r on r.id = i.idReceiptImport where i.deleteFlg = :delete" +
            " and i.idItems = :idItems and i.companyIdWork = :cid  and (r.idWareHouse = :idWareHouse or coalesce(:idWareHouse, -1) = -1) order by i.dateExpired desc")
    public List<ImportWareHouse> findAllByDeleteFlgAndIdItemsAndCompanyIdWorkOrderByDateExpiredAscInWareHouse(Integer delete, Long idItems, Long cid, Long idWareHouse);

}

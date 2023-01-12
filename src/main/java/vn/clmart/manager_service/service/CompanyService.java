package vn.clmart.manager_service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.CompanyDto;
import vn.clmart.manager_service.model.Company;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class CompanyService {

    @Autowired
    vn.clmart.manager_service.repository.CompanyRepository CompanyRepository;

    public Company create(CompanyDto reasonDto, Long cid, String uid){
        try {
            Company publisher = Company.of(reasonDto, cid, uid);
            return CompanyRepository.save(publisher);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Company update(CompanyDto CompanyDto, Long cid, String uid, Long id){
        try {
            Company Company = CompanyRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Company.setUpdateBy(uid);
            Company.setName(CompanyDto.getName());
            return CompanyRepository.save(Company);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Company getById(Long cid, String uid, Long id){
        try {
            return CompanyRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElse(new Company());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Company> search(Long cid, Pageable pageable){
        try {
            Page<Company> pageSearch = CompanyRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Company delete(Long cid, String uid, Long id){
        try {
            Company Company = CompanyRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Company.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Company.setUpdateBy(uid);
            return CompanyRepository.save(Company);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}

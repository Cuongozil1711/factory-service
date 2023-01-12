package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ReasonDto;
import vn.clmart.manager_service.model.Reason;
import vn.clmart.manager_service.repository.ReasonRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class ReasonService {

    @Autowired
    ReasonRepository ReasonRepository;

    public Reason create(ReasonDto reasonDto, Long cid, String uid){
        try {
            Reason publisher = Reason.of(reasonDto, cid, uid);
            return ReasonRepository.save(publisher);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Reason update(ReasonDto ReasonDto, Long cid, String uid, Long id){
        try {
            Reason Reason = ReasonRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Reason.setCompanyId(cid);
            Reason.setUpdateBy(uid);
            Reason.setName(ReasonDto.getName());
            return ReasonRepository.save(Reason);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Reason getById(Long cid, String uid, Long id){
        try {
            return ReasonRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Reason> search(Long cid, Pageable pageable){
        try {
            Page<Reason> pageSearch = ReasonRepository.findAllByDeleteFlg( Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Reason delete(Long cid, String uid, Long id){
        try {
            Reason Reason = ReasonRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Reason.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Reason.setCompanyId(cid);
            Reason.setUpdateBy(uid);
            return ReasonRepository.save(Reason);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}

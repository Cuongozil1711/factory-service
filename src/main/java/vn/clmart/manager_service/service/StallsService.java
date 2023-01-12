package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.StallsDto;
import vn.clmart.manager_service.model.Stalls;
import vn.clmart.manager_service.repository.StallsRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class StallsService {

    @Autowired
    StallsRepository stallsRepository;

    public Stalls create(StallsDto stallsDto, Long cid, String uid){
        try {
            Stalls stalls = Stalls.of(stallsDto, cid, uid);
            return stallsRepository.save(stalls);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Stalls update(StallsDto StallsDto, Long cid, String uid, Long id){
        try {
            Stalls Stalls = stallsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Stalls.setCompanyId(cid);
            Stalls.setUpdateBy(uid);
            Stalls.setAddress(StallsDto.getAddress());
            Stalls.setName(StallsDto.getName());
            Stalls.setImage(StallsDto.getImage());
            return stallsRepository.save(Stalls);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Stalls getById(Long cid, String uid, Long id){
        try {
            return stallsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Stalls> search(Long cid, Pageable pageable){
        try {
            Page<Stalls> pageSearch = stallsRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Stalls delete(Long cid, String uid, Long id){
        try {
            Stalls Stalls = stallsRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Stalls.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Stalls.setCompanyId(cid);
            Stalls.setUpdateBy(uid);
            return stallsRepository.save(Stalls);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

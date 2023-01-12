package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PublisherDto;
import vn.clmart.manager_service.model.Publisher;
import vn.clmart.manager_service.repository.PublisherRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class PublisherService {

    @Autowired
    PublisherRepository PublisherRepository;

    public Publisher create(PublisherDto PublisherDto, Long cid, String uid){
        try {
            Publisher publisher = Publisher.of(PublisherDto, cid, uid);
            return PublisherRepository.save(publisher);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Publisher update(PublisherDto PublisherDto, Long cid, String uid, Long id){
        try {
            Publisher Publisher = PublisherRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Publisher.setCompanyId(cid);
            Publisher.setUpdateBy(uid);
            Publisher.setAddress(PublisherDto.getAddress());
            Publisher.setName(PublisherDto.getName());
            return PublisherRepository.save(Publisher);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Publisher getById(Long cid, String uid, Long id){
        try {
            return PublisherRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Publisher> search(Long cid, Pageable pageable){
        try {
            Page<Publisher> pageSearch = PublisherRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Publisher delete(Long cid, String uid, Long id){
        try {
            Publisher Publisher = PublisherRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Publisher.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Publisher.setCompanyId(cid);
            Publisher.setUpdateBy(uid);
            return PublisherRepository.save(Publisher);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}

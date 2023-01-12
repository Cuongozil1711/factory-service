package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.CategoryDto;
import vn.clmart.manager_service.model.Category;
import vn.clmart.manager_service.repository.CategoryRepository;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.MapUntils;

@Service
@Transactional
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public Category create(CategoryDto CategoryDto, Long cid, String uid){
        try {
            Category category = Category.of(CategoryDto, cid, uid);
            return categoryRepository.save(category);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Category update(CategoryDto categoryDto, Long cid, String uid, Long id){
        try {
            Category item = categoryRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            MapUntils.copyWithoutAudit(categoryDto, item);
            item.setCompanyId(cid);
            item.setUpdateBy(uid);
            return categoryRepository.save(item);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Category getById(Long cid, String uid, Long id){
        try {
            return categoryRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Category> search(Long cid, Pageable pageable){
        try {
            Page<Category> pageSearch = categoryRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Category delete(Long cid, String uid, Long id){
        try {
            Category Category = categoryRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Category.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Category.setUpdateBy(uid);
            return categoryRepository.save(Category);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}

package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.PromotionDto;
import vn.clmart.manager_service.dto.PromotionResponseDto;
import vn.clmart.manager_service.model.Condition;
import vn.clmart.manager_service.model.ItemsDonate;
import vn.clmart.manager_service.model.Promotion;
import vn.clmart.manager_service.repository.ConditionRepository;
import vn.clmart.manager_service.repository.ItemsDonateRepository;
import vn.clmart.manager_service.repository.PromotionRepository;
import vn.clmart.manager_service.untils.Constants;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PromotionService {

    @Autowired
    PromotionRepository PromotionRepository;

    @Autowired
    ConditionRepository conditionRepository;

    @Autowired
    ItemsDonateRepository itemsDonateRepository;

    public Promotion create(PromotionDto promotionDto, Long cid, String uid){
        try {
            Promotion promotion = Promotion.of(promotionDto, cid, uid);
            promotion.setCode("KM" + new Date().getTime());
            promotion = PromotionRepository.save(promotion);
            // nếu loại điều kiện là theo sản phẩm thì thêm loại điệu kiện
            if(promotionDto.getTypePromotion().equals("product")){
                Condition condition = new Condition();
                condition.setTypeCondition(promotionDto.getTypeCondition());
                condition.setTotalPrice(promotionDto.getTotalPrice());
                condition.setTotalQuantity(promotionDto.getTotalQuantity());
                condition.setCompanyId(cid);
                condition.setCreateBy(uid);
                condition = conditionRepository.save(condition);
                promotion.setIdCondition(condition.getId());
                PromotionRepository.save(promotion);
            }else if(promotionDto.getTypePromotion().equals("donate")){
                Condition condition = new Condition();
                condition.setTypeCondition(promotionDto.getTypeCondition());
                condition.setTotalPrice(promotionDto.getTotalPrice());
                condition.setTotalQuantity(promotionDto.getTotalQuantity());
                condition.setCompanyId(cid);
                condition.setCreateBy(uid);
                condition = conditionRepository.save(condition);
                promotion.setIdCondition(condition.getId());
                ItemsDonate itemsDonate = new ItemsDonate();
                itemsDonate.setIdItems(promotionDto.getIdItemsDonate());
                itemsDonate.setQuanlity(promotionDto.getQuantity());
                itemsDonate.setCompanyId(cid);
                itemsDonate.setCreateBy(uid);
                itemsDonate = itemsDonateRepository.save(itemsDonate);
                promotion.setIdItemsDonate(itemsDonate.getId());
                PromotionRepository.save(promotion);
            }

            return PromotionRepository.save(promotion);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion update(PromotionDto promotionDto, Long cid, String uid, Long id){
        try {
            Promotion promotion = PromotionRepository.findByIdAndCompanyId(id, cid).orElseThrow();
            promotion.setCode(promotionDto.getCode());
            promotion.setName(promotionDto.getName());
            promotion.setPercent(promotionDto.getPercent());
            promotion.setIdItems(promotionDto.getIdItems());
            promotion.setPrice(promotionDto.getPrice());
            promotion.setDateFrom(promotionDto.getStartDate());
            promotion.setDateEnd(promotionDto.getEndDate());
            promotion.setType(promotionDto.getType());
            promotion.setTypePromotion(promotionDto.getTypePromotion());
            promotion.setDeleteFlg(promotionDto.getDeleteFlg());
            promotion = PromotionRepository.save(promotion);
            // nếu loại điều kiện là theo sản phẩm thì thêm loại điệu kiện
            if(promotionDto.getTypePromotion().equals("product")) {
                if (promotion.getIdCondition() != null) {
                    Condition condition = conditionRepository.findByIdAndCompanyId(promotion.getIdCondition(), cid).orElse(null);
                    if (condition != null) {
                        condition.setTypeCondition(promotionDto.getTypeCondition());
                        condition.setTotalPrice(promotionDto.getTotalPrice());
                        condition.setTotalQuantity(promotionDto.getTotalQuantity());
                        condition.setCompanyId(cid);
                        condition.setUpdateBy(uid);
                        conditionRepository.save(condition);
                    }
                }
            }
            else if(promotionDto.getTypePromotion().equals("donate")){
                if (promotion.getIdCondition() != null) {
                    Condition condition = conditionRepository.findByIdAndCompanyId(promotion.getIdCondition(), cid).orElse(null);
                    if (condition != null) {
                        condition.setTypeCondition(promotionDto.getTypeCondition());
                        condition.setTotalPrice(promotionDto.getTotalPrice());
                        condition.setTotalQuantity(promotionDto.getTotalQuantity());
                        condition.setCompanyId(cid);
                        condition.setUpdateBy(uid);
                        conditionRepository.save(condition);
                    }
                }
                if(promotion.getIdItemsDonate() != null){
                    ItemsDonate itemsDonate = itemsDonateRepository.findByIdAndCompanyId(promotion.getIdItemsDonate(), cid).orElse(null);
                    itemsDonate.setIdItems(promotionDto.getIdItemsDonate());
                    itemsDonate.setQuanlity(promotionDto.getQuantity());
                    itemsDonate.setUpdateBy(uid);
                    itemsDonateRepository.save(itemsDonate);
                }
            }
            return  promotion;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion getById(Long cid, String uid, Long id){
        try {
            return PromotionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<PromotionResponseDto> search(Long cid, Pageable pageable){
        try {
            Page<Promotion> pageSearch = PromotionRepository.findAllByCompanyId(cid,  pageable);
            List<PromotionResponseDto> list = pageSearch.getContent().stream().map(promotion -> {
                PromotionResponseDto promotionResponseDto = new PromotionResponseDto();
                promotionResponseDto.setPromotion(promotion);
                if(promotion.getIdCondition() != null){
                    Condition condition = conditionRepository.findByIdAndCompanyId(promotion.getIdCondition(), cid).orElse(new Condition());
                    promotionResponseDto.setCondition(condition);
                }
                if(promotion.getIdItemsDonate() != null){
                    ItemsDonate itemsDonate = itemsDonateRepository.findByIdAndCompanyId(promotion.getIdItemsDonate(), cid).orElse(new ItemsDonate());
                    promotionResponseDto.setItemsDonate(itemsDonate);
                }
                return promotionResponseDto;
            }).collect(Collectors.toList());

            return new PageImpl(list, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Promotion delete(Long cid, String uid, Long id){
        try {
            Promotion Promotion = PromotionRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            Promotion.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            Promotion.setCompanyId(cid);
            Promotion.setUpdateBy(uid);
            return PromotionRepository.save(Promotion);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<PromotionResponseDto> getByItemsId(Long cid, Long idItems){
        try {
            List<Promotion> promotions = PromotionRepository.getByItems(cid, Constants.DELETE_FLG.NON_DELETE, idItems);
            return promotions.stream().map(promotion -> {
                PromotionResponseDto promotionResponseDto = new PromotionResponseDto();
                promotionResponseDto.setPromotion(promotion);
                if(promotion.getIdCondition() != null){
                    Condition condition = conditionRepository.findByIdAndCompanyId(promotion.getIdCondition(), cid).orElse(new Condition());
                    promotionResponseDto.setCondition(condition);
                }
                if(promotion.getIdItemsDonate() != null){
                    ItemsDonate itemsDonate = itemsDonateRepository.findByIdAndCompanyId(promotion.getIdItemsDonate(), cid).orElse(new ItemsDonate());
                    promotionResponseDto.setItemsDonate(itemsDonate);
                }
                return promotionResponseDto;
            }).collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}

package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.mapper.CodeMapper;
import com.fs.tool.memory.dao.model.CommonWordDO;
import com.fs.tool.memory.dao.query.Mode;
import com.fs.tool.memory.dao.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 联想词管理器
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
@Service
public class CodeRepository implements ICodeRepository {
    @Autowired
    private CodeMapper codeRepository;
    @Autowired
    private Context context;


    /**
     * 是否有联想词数据
     *
     * @return
     */
    @Override
    public boolean hasCodes() {
        return count(Query.builder().group(context.currentGroup).build()) > 0;
    }


    /**
     * 清除group 下所有联想词
     */
    @Override
    @Transactional
    public void clearAll() {
        codeRepository.deleteAllByWordGroup(context.currentGroup);
    }


    /**
     * 保存所有
     *
     * @param commonWords
     */
    @Override
    public void saveAll(List<CommonWordDO> commonWords) {
        codeRepository.saveAll(commonWords);
    }

    /**
     * 保存联想词
     *
     * @param word
     */
    @Override
    public void save(CommonWordDO word) {
        codeRepository.save(word);
    }

    /**
     * 条件查询
     *
     * @param condition 条件
     * @return
     */

    @Override
    public List<CommonWordDO> queryByCondition(Query condition) {
        condition.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(condition);
        return codeRepository.findAll(codeSpecification);
    }

    /**
     * 条件查询
     *
     * @param condition   条件
     * @param pageRequest 分页条件
     * @return
     */
    @Override
    public Page<CommonWordDO> queryByCondition(Query condition, PageRequest pageRequest) {
        condition.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(condition);
        return codeRepository.findAll(codeSpecification, pageRequest);
    }

    /**
     * 查询单条
     *
     * @param query
     * @return
     */
    @Override
    public Optional<CommonWordDO> queryOne(Query query) {
        query.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(query);
        return codeRepository.findOne(codeSpecification);
    }

    /**
     * 统计count
     */
    @Override
    public long count(Query query) {
        query.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(query);
        return codeRepository.count(codeSpecification);
    }

    /**
     * 删除联想词
     *
     * @param id 主键
     */
    @Override
    public void delete(String id) {
        codeRepository.deleteById(id);
    }

    /**
     * 条件删除
     *
     * @param query
     * @return size of deleted
     */
    @Override
    public int deleteByCondition(Query query) {
        query.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(query);
        List<CommonWordDO> all = codeRepository.findAll(codeSpecification);
        for (CommonWordDO commonWord : all) {
            codeRepository.deleteById(commonWord.getId());
        }
        return all.size();
    }

    /**
     * 通用条件查询
     */
    private static class CodeSpecification implements Specification<CommonWordDO> {
        private Query condition;

        public CodeSpecification(Query condition) {
            this.condition = condition;
        }

        @Override
        public Predicate toPredicate(Root<CommonWordDO> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();
            String group = condition.getGroup();
            if (group != null) {
                predicates.add(criteriaBuilder.equal(root.get("wordGroup"), group));
            }
            String code = condition.getCode();
            if (!StringUtils.isEmpty(code)) {
                Mode codeMode = condition.getCodeMode();
                switch (codeMode) {
                    case EXACT:
                        predicates.add(criteriaBuilder.equal(root.get("key"), code));
                        break;
                    case PREFIX:
                        predicates.add(criteriaBuilder.like(root.get("key"), code + "%"));
                        break;
                    case SUFFIX:
                        predicates.add(criteriaBuilder.like(root.get("key"), "%" + code));
                        break;
                }
            }
            Boolean isRemembered = condition.getIsRemembered();
            if (isRemembered != null) {
                predicates.add(criteriaBuilder.equal(root.get("remembered"), isRemembered));
            }
            Boolean hasWord = condition.getExistDefinition();
            if (hasWord != null) {
                if (hasWord) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("definition")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("definition")));
                }
            }


            Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
            query.where(predicateArray);
            return null;
        }
    }
}

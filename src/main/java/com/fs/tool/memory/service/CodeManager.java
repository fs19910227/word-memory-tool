package com.fs.tool.memory.service;

import com.fs.tool.memory.command.Context;
import com.fs.tool.memory.dao.model.CommonWord;
import com.fs.tool.memory.dao.repository.CodeRepository;
import com.fs.tool.memory.model.Query;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CodeManager {
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private Context context;

    /**
     * 是否有联想词数据
     *
     * @return
     */
    public boolean hasCodes() {
        return count(Query.builder().build()) > 0;
    }

    /**
     * 清除group 下所有联想词
     */
    @Transactional
    public void clearAll() {
        codeRepository.deleteAllByWordGroup(context.currentGroup);
    }


    /**
     * 保存联想词
     *
     * @param code
     * @param overwrite 是否覆盖
     */
    public void save(CommonWord code, boolean overwrite) {
        Optional<CommonWord> commonWord = codeRepository.findByKeyAndWordGroup(code.getKey(), code.getWordGroup());
        if (commonWord.isPresent()) {
            CommonWord word = commonWord.get();
            if (overwrite) {
                code.setId(word.getId());
                codeRepository.save(code);
            }
        } else {
            codeRepository.save(code);
        }
    }

    /**
     * 保存所有
     *
     * @param commonWords
     */
    public void saveAll(List<CommonWord> commonWords) {
        codeRepository.saveAll(commonWords);
    }


    /**
     * 保存联想词
     *
     * @param word
     */
    public void save(CommonWord word) {
        save(word, true);
    }

    /**
     * 条件查询
     *
     * @return
     */

    public List<CommonWord> queryByCondition(Query condition) {
        condition.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(condition);
        return codeRepository.findAll(codeSpecification);
    }

    /**
     * 查询单条
     *
     * @param query
     * @return
     */
    public Optional<CommonWord> queryOne(Query query) {
        query.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(query);
        return codeRepository.findOne(codeSpecification);
    }

    /**
     * 统计count
     */
    public long count(Query query) {
        query.setGroup(context.currentGroup);
        CodeSpecification codeSpecification = new CodeSpecification(query);
        return codeRepository.count(codeSpecification);
    }


    /**
     * 通过row查询codes
     *
     * @param row row index
     * @return
     */
    public List<CommonWord> queryByRowIndex(String row) {
        Query query = new Query();
        query.setCode(row);
        return queryByCondition(query);
    }

    /**
     * 通过编码查询
     *
     * @param code
     * @return
     */
    public Optional<CommonWord> queryByCode(String code) {
        Query query = new Query();
        query.setCode(code);
        return queryOne(query);
    }

    /**
     * 通用条件查询
     */
    private static class CodeSpecification implements Specification<CommonWord> {
        private Query condition;

        public CodeSpecification(Query condition) {
            this.condition = condition;
        }

        @Override
        public Predicate toPredicate(Root<CommonWord> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();
            String group = condition.getGroup();
            if (group != null) {
                predicates.add(criteriaBuilder.equal(root.get("wordGroup"), group));
            }
            String code = condition.getCode();
            if (!StringUtils.isEmpty(code)) {
                predicates.add(criteriaBuilder.like(root.get("key"), code + "%"));
            }
            Boolean isRemembered = condition.getIsRemembered();
            if (isRemembered != null) {
                predicates.add(criteriaBuilder.equal(root.get("remembered"), isRemembered));
            }
            Boolean hasWord = condition.getHasWord();
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

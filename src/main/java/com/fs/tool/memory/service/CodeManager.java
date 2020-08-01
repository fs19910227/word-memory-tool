package com.fs.tool.memory.service;

import com.fs.tool.memory.command.Context;
import com.fs.tool.memory.dao.model.CommonWord;
import com.fs.tool.memory.dao.model.WordGroup;
import com.fs.tool.memory.dao.repository.CodeRepository;
import com.fs.tool.memory.dao.repository.GroupRepository;
import com.fs.tool.memory.model.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import java.util.UUID;

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
    private GroupRepository groupRepository;
    @Autowired
    private Context context;

    /**
     * 是否存在分组
     */
    public boolean existGroup(WordGroup wordGroup) {
        Example<WordGroup> of = Example.of(wordGroup);
        of.getMatcher().withMatcher("name", matcher -> matcher.exact());
        return groupRepository.exists(of);
    }

    /**
     * 获取分组
     *
     * @return
     */
    public Optional<WordGroup> findGroup(String name) {
        WordGroup wordGroup = new WordGroup();
        wordGroup.setName(name);
        Example<WordGroup> of = Example.of(wordGroup);
        of.getMatcher().withMatcher("name", matcher -> matcher.exact());
        return groupRepository.findOne(of);
    }

    /**
     * 新增分组
     */
    public void addGroup(WordGroup wordGroup) {
        wordGroup.setId(UUID.randomUUID().toString());
        groupRepository.save(wordGroup);
    }

    /**
     * 所有分组信息
     *
     * @return
     */
    public List<WordGroup> groups() {
        return groupRepository.findAll();
    }

    /**
     * 是否有联想词数据
     *
     * @return
     */
    public boolean hasCodes() {
        return count(Query.builder().group(context.currentGroup).build()) > 0;
    }


    /**
     * 清除group 下所有联想词
     */
    @Transactional
    public void clearAll() {
        codeRepository.deleteAllByWordGroup(context.currentGroup);
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
        codeRepository.save(word);
    }

    /**
     * 条件查询
     *
     * @param condition 查询多条
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
     * 删除联想词
     *
     * @param id
     */
    public void delete(String id) {
        codeRepository.deleteById(id);
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
            String prefix = condition.getPrefix();
            if (!StringUtils.isEmpty(prefix)) {
                predicates.add(criteriaBuilder.like(root.get("key"), prefix + "%"));
            } else {
                String code = condition.getCode();
                if (!StringUtils.isEmpty(code)) {
                    predicates.add(criteriaBuilder.equal(root.get("key"), code));
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

package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.model.CommonWordDO;
import com.fs.tool.memory.dao.model.WordGroupDO;
import com.fs.tool.memory.dao.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author zhaofushan
 * @date 2020/8/6 0006 20:43
 */
public interface ICodeRepository {
    boolean existGroup(WordGroupDO wordGroup);

    Optional<WordGroupDO> findGroup(String name);

    void addGroup(WordGroupDO wordGroup);

    List<WordGroupDO> groups();

    boolean hasCodes();

    @Transactional
    void clearAll();

    void saveAll(List<CommonWordDO> commonWords);

    void save(CommonWordDO word);

    List<CommonWordDO> queryByCondition(Query condition);


    Page<CommonWordDO> queryByCondition(Query condition, PageRequest pageRequest);

    Optional<CommonWordDO> queryOne(Query query);

    long count(Query query);

    void delete(String id);

    int deleteByCondition(Query query);
}

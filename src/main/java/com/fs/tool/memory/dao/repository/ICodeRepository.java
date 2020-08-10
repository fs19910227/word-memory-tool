package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.model.CommonWordDO;
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
    boolean hasCodes();

    @Transactional
    void clearAll();

    void saveAll(List<CommonWordDO> commonWords);

    void save(CommonWordDO word);

    List<CommonWordDO> queryByCondition(Query condition);


    Page<CommonWordDO> queryByCondition(Query condition, PageRequest pageRequest);

    Optional<CommonWordDO> queryOne(Query query);


    Optional<CommonWordDO> queryById(String key);

    long count(Query query);

    void delete(String id);

    int deleteByCondition(Query query);
}

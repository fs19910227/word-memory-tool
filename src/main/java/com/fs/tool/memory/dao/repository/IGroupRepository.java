package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.model.WordGroupDO;

import java.util.List;
import java.util.Optional;

/**
 * @author zhaofushan
 * @date 2020/8/6 0006 20:43
 */
public interface IGroupRepository {
    boolean exist(WordGroupDO wordGroup);

    Optional<WordGroupDO> defaultGroup();

    Optional<WordGroupDO> queryOne(String name);

    void add(WordGroupDO wordGroup);

    void saveAll(Iterable<WordGroupDO> groups);

    List<WordGroupDO> groups();

    void save(WordGroupDO groupDO);

    void deleteById(String id);
}

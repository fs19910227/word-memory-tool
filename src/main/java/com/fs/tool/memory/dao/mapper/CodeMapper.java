package com.fs.tool.memory.dao.mapper;

import com.fs.tool.memory.dao.model.CommonWordDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface CodeMapper extends JpaRepository<CommonWordDO, String>, JpaSpecificationExecutor<CommonWordDO> {
    /**
     * delete all by wordGroup
     *
     * @param group
     */
    void deleteAllByWordGroup(String group);

    boolean existsByKeyAndWordGroup(String key, String group);

    Optional<CommonWordDO> findByKeyAndWordGroup(String key, String group);

}
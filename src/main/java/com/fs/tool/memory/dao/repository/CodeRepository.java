package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.model.CommonWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface CodeRepository extends JpaRepository<CommonWord, String>, JpaSpecificationExecutor<CommonWord> {
    /**
     * delete all by wordGroup
     *
     * @param group
     */
    void deleteAllByWordGroup(String group);

    boolean existsByKeyAndWordGroup(String key, String group);

    Optional<CommonWord> findByKeyAndWordGroup(String key, String group);


}
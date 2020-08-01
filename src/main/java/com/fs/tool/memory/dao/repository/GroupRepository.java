package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.model.WordGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface GroupRepository extends JpaRepository<WordGroup, String>, JpaSpecificationExecutor<WordGroup> {


}
package com.fs.tool.memory.dao.mapper;

import com.fs.tool.memory.dao.model.WordGroupDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface GroupMapper extends JpaRepository<WordGroupDO, String>, JpaSpecificationExecutor<WordGroupDO> {


}
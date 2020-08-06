package com.fs.tool.memory.core;

import com.fs.tool.memory.command.GroupManagementCommand;
import com.fs.tool.memory.domain.bo.WordTestBO;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaofushan
 * @date 2020/7/30 0030 21:31
 */
@Component
public class Context {
    public String currentGroup = GroupManagementCommand.DEFAULT_GROUP;
    /**
     * 保存test
     * key->group
     * value->test bo
     */
    public ConcurrentHashMap<String, WordTestBO> savedTestMap = new ConcurrentHashMap<>();
}

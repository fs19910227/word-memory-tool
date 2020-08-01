package com.fs.tool.memory.core;

import com.fs.tool.memory.command.GroupManagementCommand;
import org.springframework.stereotype.Component;

/**
 * @author zhaofushan
 * @date 2020/7/30 0030 21:31
 */
@Component
public class Context {
    public String currentGroup = GroupManagementCommand.DEFAULT_GROUP;
}

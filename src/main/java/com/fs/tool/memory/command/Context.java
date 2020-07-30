package com.fs.tool.memory.command;

import org.springframework.stereotype.Component;

/**
 * @author zhaofushan
 * @date 2020/7/30 0030 21:31
 */
@Component
public class Context {
    public String currentGroup = ManagementCommand.DEFAULT_GROUP;
}

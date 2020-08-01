package com.fs.tool.memory.command;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

/**
 * 分组命令管理器
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface GroupManagementCommand {


    String DEFAULT_GROUP = "DEFAULT";

    @ShellMethod(value = "switch group", key = {"use"})
    void chooseGroup(@ShellOption(value = {"-g", "-group"},
            defaultValue = DEFAULT_GROUP) String group);

    @ShellMethod(value = "list all groups", key = {"groups"})
    List<String> groups();
}

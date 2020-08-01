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

    @ShellMethod(value = "列出所有分组信息", key = {"groups"})
    List<String> groups();


    @ShellMethod(value = "切换分组", key = "use")
    void chooseGroup(@ShellOption(value = {"-g", "-group", "联想词数据会按照分组隔离"},
            defaultValue = DEFAULT_GROUP) String group);

}

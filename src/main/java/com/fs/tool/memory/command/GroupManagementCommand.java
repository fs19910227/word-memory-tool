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

    void chooseGroup(String group);

    @ShellMethod(value = "list all groups", key = {"groups"})
    List<String> groups();

    @ShellMethod(value = "delete group by name", key = {"group-delete", "dg"})
    String delete(@ShellOption(value = {"-name"}, help = "exact match") String name);

    @ShellMethod(value = "edit  group", key = {"group-edit"})
    String edit(@ShellOption(defaultValue = ShellOption.NULL) String name);
}

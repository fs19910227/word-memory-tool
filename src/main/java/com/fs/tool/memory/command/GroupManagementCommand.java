package com.fs.tool.memory.command;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
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

    @ShellMethod(value = "delete group by name", key = {"delete-group", "dg"})
    String delete(@ShellOption(value = {"-name"}, help = "exact match") String name);

    @ShellMethod(value = "edit  group", key = {"groups"})
    String edit(@Size(min = 1) String name);
}

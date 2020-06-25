package com.fs.tool.memory.command;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.util.List;

public interface ManagementCommand {
    @ShellMethod(value = "初始化数据", key = "init")
    void initData();


    @ShellMethod(value = "查询编码,默认不显示空码。", key = {"query", "q"})
    List<String> query(@ShellOption(defaultValue = "") @Size(max = 2) String code,
                       @ShellOption(value = "-e", defaultValue = "true") boolean onlyExist,
                       @ShellOption(value = "-r") Boolean remembered);

    @ShellMethod(value = "同步数据到数据库", key = "sync")
    void sync();

    @ShellMethod(value = "编辑联想词", key = {"edit", "e"})
    String edit(@Size(min = 2, max = 2) String code);

    @ShellMethod(value = "联想词录入模式", key = {"i", "input"})
    void typeIn(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行录入数据") @Size(min = 0, max = 1) String row,
                @ShellOption(defaultValue = "true", value = {"-sync", "-s"}, help = "每次录入自动同步到数据库，默认为true") boolean autoSync);

    @ShellMethod(value = "记忆测试,记忆所有没有记住的编码", key = {"t", "test"})
    void test();
}

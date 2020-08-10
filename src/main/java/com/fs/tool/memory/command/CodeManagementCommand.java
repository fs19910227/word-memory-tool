package com.fs.tool.memory.command;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 命令管理器
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface CodeManagementCommand {


    @ShellMethod(value = "init data", key = "init")
    void initData();


    @ShellMethod(value = "show statistic info", key = {"info", "statistic"})
    String statistic();

    @ShellMethod(value = "query word by code", key = {"query", "q"})
    List<String> query(@ShellOption(defaultValue = ShellOption.NULL) String code,
                       @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,default false") boolean suffix,
                       @ShellOption(value = "-e", defaultValue = "", help = "exist definition") Boolean existDefinition,
                       @ShellOption(value = "-r", defaultValue = "", help = "is remembered") Boolean remembered);

    @ShellMethod(value = "edit word", key = {"edit", "e"})
    String edit(@Size(min = 1) String code);


    @ShellMethod(value = "add new word", key = {"add", "a"})
    String add(@Size(min = 1) String code,
               @ShellOption(defaultValue = ShellOption.NULL) String definition,
               @ShellOption(defaultValue = ShellOption.NULL) String description);


    @ShellMethod(value = "delete word by code", key = {"delete", "d"})
    String delete(@ShellOption(value = {"-value", "-v"}, help = "exact match") String code,
                  @ShellOption(defaultValue = "false", value = {"-prefix", "-p"}, help = "prefix match,default false") boolean prefix,
                  @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,default false") boolean suffix
    );


    @ShellMethod(value = "memory test", key = {"t", "test"})
    void test(@ShellOption(defaultValue = "", help = "code prefix match") @Size() String prefix,
              @ShellOption(defaultValue = Integer.MAX_VALUE + "", value = "-limit", help = "words limit，default no limit") Integer limit,
              @ShellOption(defaultValue = "false", value = "-review", help = "review word，default false") Boolean isReview,
              @ShellOption(defaultValue = "false", value = "-random", help = "random word，default false") Boolean isRandom,
              @ShellOption(defaultValue = "true", value = "-repeat", help = "repeat until remember at least once，default false") Boolean repeat);

    @ShellMethod(value = "Delete all", key = "drop")
    String deleteAll();

    @ShellMethod(value = "import data using excel file", key = "import")
    void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                    @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite);
}

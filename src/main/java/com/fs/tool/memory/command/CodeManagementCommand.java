package com.fs.tool.memory.command;

import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.io.IOException;
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


    @ShellMethod(value = "query word by code", key = {"query", "q"})
    List<String> query(@ShellOption(defaultValue = ShellOption.NULL) String code,
                       @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,defult false") boolean suffix,
                       @ShellOption(value = "-e", defaultValue = "", help = "exist definition") Boolean existDefinition,
                       @ShellOption(value = "-r", defaultValue = "", help = "is remembered") Boolean remembered);

    @ShellMethod(value = "edit word", key = {"edit", "e"})
    String edit(@Size(min = 1) String code);


    @ShellMethod(value = "add new word", key = {"add", "a"})
    String add(@Size(min = 1) String code,
               @ShellOption(defaultValue = ShellOption.NULL) String definition,
               @ShellOption(defaultValue = ShellOption.NULL) String desctrption);


    @ShellMethod(value = "delete word by code", key = {"delete", "d"})
    String delete(@ShellOption(value = {"-value", "-v"}, help = "exact mathc") String code,
                  @ShellOption(defaultValue = "false", value = {"-prefix", "-p"}, help = "prefix match,defalut false") boolean prefix,
                  @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,defalut false") boolean suffix
    );


    @ShellMethod(value = "memory test", key = {"t", "test"})
    void test(@ShellOption(defaultValue = "", value = {"-r", "-row", "-prefix"}, help = "code prefix match") @Size(min = 0) String prefix,
              @ShellOption(defaultValue = "false", value = "--review", help = "review word，default false") Boolean isReview,
              @ShellOption(defaultValue = "false", value = "--random", help = "random word，default false") Boolean isRandom,
              @ShellOption(defaultValue = "false", value = "--repeat", help = "repeat until remember at least once，default false") Boolean repeat) throws IOException;

    @ShellMethod(value = "Delete all", key = "drop")
    String deleteAll();

    @ShellMethod(value = "import data using execel file", key = "import")
    void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                    @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite);
}

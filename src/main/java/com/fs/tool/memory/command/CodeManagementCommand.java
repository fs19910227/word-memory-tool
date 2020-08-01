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
                       @ShellOption(value = "-e", defaultValue = "", help = "是否存在定义") Boolean existDefinition,
                       @ShellOption(value = "-r", defaultValue = "", help = "是否记住") Boolean remembered);


    @ShellMethod(value = "edit word", key = {"edit", "e"})
    String edit(@Size(min = 1) String code);


    @ShellMethod(value = "add new word", key = {"add", "a"})
    String add(@Size(min = 1) String code,
               @ShellOption(defaultValue = ShellOption.NULL) String definition,
               @ShellOption(defaultValue = ShellOption.NULL) String desctrption);


    @ShellMethod(value = "delete word by code", key = {"delete", "d"})
    String delete(@Size(min = 1) String code);

    @ShellMethod(value = "memory test", key = {"t", "test"})
    void test(@ShellOption(defaultValue = "", value = {"-r", "-row", "-prefix"}, help = "指定code prefix进行测试") @Size(min = 0, max = 1) String prefix,
              @ShellOption(defaultValue = "false", value = "--review", help = "是否是复习模式，默认false") Boolean isReview,
              @ShellOption(defaultValue = "false", value = "--random", help = "是否随机，默认false") Boolean isRandom) throws IOException;

    @ShellMethod(value = "Delete all", key = "drop")
    String deleteAll();

    @ShellMethod(value = "import data using execel file", key = "import")
    void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                    @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite);
}

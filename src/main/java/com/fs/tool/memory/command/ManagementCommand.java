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
public interface ManagementCommand {


    static final String DEFAULT_GROUP = "DEFAULT";

    @ShellMethod(value = "初始化数据", key = "init")
    void initData();

    @ShellMethod(value = "查询编码,默认不显示空码。", key = {"query", "q"})
    List<String> query(@ShellOption(defaultValue = "") @Size(max = 2) String code,
                       @ShellOption(value = "-e", defaultValue = "true") boolean onlyExist,
                       @ShellOption(value = "-r") Boolean remembered);

    @ShellMethod(value = "编辑联想词", key = {"edit", "e"})
    String edit(@Size(min = 2, max = 2) String code);


    @ShellMethod(value = "记忆测试,记忆所有没有记住的编码", key = {"t", "test"})
    void test(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行进行测试") @Size(min = 0, max = 1) String row,
              @ShellOption(defaultValue = "false", value = "--review", help = "是否是复习模式，默认false") Boolean isReview,
              @ShellOption(defaultValue = "false", value = "--random", help = "是否随机，默认false") Boolean isRandom) throws IOException;

    @ShellMethod(value = "切换分组", key = "use")
    void chooseGroup(@ShellOption(value = {"-g", "-group", "联想词数据会按照分组隔离"},
            defaultValue = DEFAULT_GROUP) String group);


    @ShellMethod(value = "Excel import,需遵循导入模板", key = "import")
    void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                    @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite);

    @ShellMethod(value = "Delete all", key = "drop")
    String deleteAll();
}

package com.fs.tool.memory.command.impl;

import com.fs.tool.memory.command.GlobalCommand;
import com.fs.tool.memory.domain.bo.WordCopyBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * 全局命令
 *
 * @author zhaofushan
 * @date 2020/8/29 0029 22:11
 */
@ShellComponent
@ShellCommandGroup("global operate")
public class GlobalCommandImpl implements GlobalCommand {
    @Autowired
    private WordCopyBO wordCopyBO;

    @Override
    @ShellMethod(value = "copy data from group1 to group2", key = {"copyAll"})
    public String copyAll(String fromGroup,
                          String toGroup,
                          @ShellOption(value = "-o", defaultValue = "false", help = "overwrite or not,default false") Boolean overwrite) {
        wordCopyBO.copyAll(fromGroup, toGroup, overwrite);
        return "ok";
    }

    @Override
    @ShellMethod(value = "copy data from group1 to group2", key = {"copy"})
    public String copy(String fromGroup,
                       String toGroup,
                       @ShellOption(value = "-o", defaultValue = "false", help = "overwrite or not,default false") Boolean overwrite) {
        wordCopyBO.copy(fromGroup, toGroup, overwrite);
        return "ok";
    }
}

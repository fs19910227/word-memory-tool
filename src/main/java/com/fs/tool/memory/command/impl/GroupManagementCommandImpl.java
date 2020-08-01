package com.fs.tool.memory.command.impl;

import com.fs.tool.memory.command.GroupManagementCommand;
import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.model.WordGroup;
import com.fs.tool.memory.service.CodeManager;
import com.fs.tool.memory.service.console.ConsoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
@ShellComponent
@ShellCommandGroup("group operate")
@Slf4j
public class GroupManagementCommandImpl implements GroupManagementCommand, ApplicationListener<ApplicationStartedEvent> {
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private Context context;
    @Autowired
    private ConsoleService consoleService;


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        context.currentGroup = DEFAULT_GROUP;
        chooseGroup(DEFAULT_GROUP);
    }

    @Override
    @ShellMethod(value = "switch group", key = {"use"})
    public void chooseGroup(@ShellOption(value = {"-g", "-group"},
            defaultValue = DEFAULT_GROUP) String group) {
        context.currentGroup = group;
        WordGroup wordGroup = new WordGroup();
        wordGroup.setName(group);
        wordGroup.setDescription(group);
        if (!codeManager.existGroup(wordGroup)) {
            codeManager.addGroup(wordGroup);
        }
        consoleService.outputLn("change group to " + group);
    }

    @Override
    @ShellMethod(value = "list all groups", key = {"groups"})
    public List<String> groups() {
        return codeManager.groups().stream().map(WordGroup::toString).collect(Collectors.toList());
    }


}

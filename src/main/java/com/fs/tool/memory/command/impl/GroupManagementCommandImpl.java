package com.fs.tool.memory.command.impl;

import com.fs.tool.memory.command.GroupManagementCommand;
import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.model.WordGroupDO;
import com.fs.tool.memory.dao.repository.IGroupRepository;
import com.fs.tool.memory.domain.service.IOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;
import java.util.Optional;
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
    private IGroupRepository groupRepository;
    @Autowired
    private Context context;
    @Autowired
    private IOService consoleService;


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        context.currentGroup = DEFAULT_GROUP;
        chooseGroup(DEFAULT_GROUP);
    }

    @Override
    @ShellMethod(value = "switch group", key = {"use"})
    public void chooseGroup(@ShellOption(value = {"-g", "-group"},
            defaultValue = DEFAULT_GROUP) String group) {
        boolean chooseDefault = group.equals(DEFAULT_GROUP);
        Optional<WordGroupDO> optional;
        if (chooseDefault) {
            optional = groupRepository.defaultGroup();
        } else {
            optional = groupRepository.queryOne(group);
        }
        WordGroupDO wordGroup = optional.orElseGet(() -> {
            WordGroupDO wg = new WordGroupDO()
                    .setName(group)
                    .setIsDefault(chooseDefault)
                    .setDescription(group);
            groupRepository.add(wg);
            return wg;
        });
        context.currentGroup = wordGroup.getName();
        consoleService.outputLn("change to group :" + wordGroup.getName());
    }

    @Override
    @ShellMethod(value = "list all groups", key = {"groups"})
    public List<String> groups() {
        return groupRepository.groups().stream().map(WordGroupDO::toString).collect(Collectors.toList());
    }

    @Override

    @ShellMethod(value = "delete group by name", key = {"group-delete", "dg"})
    public String delete(@ShellOption(value = {"-name"}, help = "exact match") String name) {
        groupRepository.queryOne(name).ifPresent(group -> {
            groupRepository.deleteById(group.getId());
            chooseGroup(DEFAULT_GROUP);
        });
        return "ok";
    }

    @Override
    @ShellMethod(value = "edit  group", key = {"group-edit"})
    public String edit(@ShellOption(defaultValue = ShellOption.NULL) String name) {
        if (name == null) {
            name = context.currentGroup;
        }
        Optional<WordGroupDO> group = groupRepository.queryOne(name);
        String result;
        if (!group.isPresent()) {
            result = "group not find";
            return result;
        }
        WordGroupDO groupDO = group.get();

        String note = String.format("Edit group,group:%s,description:%s,isDefault:%s。(type Enter to skip select)",
                groupDO.getName(),
                groupDO.getDescription(),
                groupDO.getIsDefault().toString());
        consoleService.outputLn(note);
        //edit description
        String input = consoleService.readLine("please input new description:");
        if (!"".equals(input)) {
            groupDO.setDescription(input);
        }
        //set default
        input = consoleService.readLine("do you want to set this group default?y/n:");
        boolean isDefault = "y".equals(input.toLowerCase());
        if (isDefault) {
            groupDO.setIsDefault(true);
        }

        if (isDefault) {
            List<WordGroupDO> collect = groupRepository.groups().stream()
                    .peek(g -> g.setIsDefault(false))
                    .collect(Collectors.toList());
            groupRepository.saveAll(collect);
        }
        groupRepository.save(groupDO);
        return "ok";
    }
}

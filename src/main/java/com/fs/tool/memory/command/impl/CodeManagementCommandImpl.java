package com.fs.tool.memory.command.impl;

import com.fs.tool.memory.command.CodeManagementCommand;
import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.model.CommonWord;
import com.fs.tool.memory.model.Query;
import com.fs.tool.memory.service.CodeManager;
import com.fs.tool.memory.service.console.ConsoleService;
import com.fs.tool.memory.service.imports.DataImportService;
import com.fs.tool.memory.service.init.DataInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
@ShellComponent
@ShellCommandGroup("code operate")
@Slf4j
public class CodeManagementCommandImpl implements CodeManagementCommand {
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private DataInitService dataInitService;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private Context context;
    @Autowired
    private ConsoleService consoleService;


    @Override
    @ShellMethod(value = "init data", key = "init")
    public void initData() {
        if (codeManager.count(Query.builder().build()) > 0) {
            consoleService.outputLn("group " + context.currentGroup + " already has data!,won't init");
            return;
        }
        List<CommonWord> commonWords = dataInitService.biInit(context.currentGroup, DataInitService.DEFAULT_ALPHABET_LIST);
        codeManager.saveAll(commonWords);
        consoleService.outputLn("init data,current group:" + context.currentGroup);
    }

    @ShellMethod(value = "show statistic info", key = {"info", "statistic"})
    public String statistic() {
        long total = codeManager.count(Query.builder().build());
        double hasWords = codeManager.count(Query.builder().existDefinition(true).build());
        double rememebered = codeManager.count(Query.builder().isRemembered(true).build());
        List<String> groups = codeManager.groups().stream().map(g -> g.getName()).collect(Collectors.toList());
        String info = "当前分组:%s\n" +
                "所有分组:%s\n" +
                "联想编码总数:%d\n" +
                "有定义的联想词:%.0f,占比%.2f%%\n" +
                "已记住联想词数:%.0f,占比%.2f%%\n";
        return String.format(info, context.currentGroup, groups, total, hasWords, hasWords / total * 100, rememebered, rememebered / total * 100);
    }

    @Override
    @ShellMethod(value = "query word by code", key = {"query", "q"})
    public List<String> query(@ShellOption(defaultValue = ShellOption.NULL) String code,
                              @ShellOption(value = "-e", defaultValue = "", help = "exist definition") Boolean existDefinition,
                              @ShellOption(value = "-r", defaultValue = "", help = "is remembered") Boolean remembered) {
        List<String> result = new ArrayList<>();
        if (!codeManager.hasCodes()) {
            result.add("word not find");
            return result;
        }
        code = code == null ? null : code.toUpperCase();
        Query query = new Query();
        query.setExistDefinition(existDefinition);
        query.setIsRemembered(remembered);
        query.setPrefix(code);
        return codeManager.queryByCondition(query).stream()
                .map(info -> info.toString())
                .collect(Collectors.toList());
    }


    @Override
    @ShellMethod(value = "edit word", key = {"edit", "e"})
    public String edit(@Size(min = 1) String code) {
        CommonWord query = codeManager.queryOne(Query.builder().code(code.toUpperCase()).build()).orElse(null);
        String result;
        if (query == null) {
            result = "word not find";
            return result;
        }
        consoleService.outputLn(String.format("Edit word , code:%s,old definition:%s。(type q to exit)", query.getKey(), query.getDefinition()));
        String input = consoleService.readLine("please input new definition:");
        switch (input) {
            case "q":
                result = "quit edit mode";
                break;
            default:
                query.setDefinition(input.trim());
                codeManager.save(query);
                result = "word definition " + query.getDefinition() + " saved";
        }
        return result;
    }

    @Override
    @ShellMethod(value = "add new word", key = {"add", "a"})
    public String add(@Size(min = 1) String code,
                      @ShellOption(defaultValue = ShellOption.NULL) String definition,
                      @ShellOption(defaultValue = ShellOption.NULL) String desctrption) {
        String upperCode = code.toUpperCase();
        CommonWord oldWord = codeManager.queryOne(Query.builder().code(upperCode).build()).orElse(null);
        String result;
        if (oldWord != null) {
            result = "word already exist," + oldWord.toString();
            return result;
        }
        CommonWord commonWord = new CommonWord(upperCode, context.currentGroup, definition, desctrption, false, 0, 0);
        codeManager.save(commonWord);
        return "add word success";
    }

    @Override
    @ShellMethod(value = "delete word by code", key = {"delete", "d"})
    public String delete(@Size(min = 1) String code) {
        String upperCode = code.toUpperCase();
        CommonWord query = codeManager.queryOne(Query.builder().code(upperCode).build()).orElse(null);
        String result;
        if (query == null) {
            result = "未能查询到联想词";
            return result;
        }
        codeManager.delete(query.getId());
        return "删除成功";
    }

    @Override
    @ShellMethod(value = "memory test", key = {"t", "test"})
    public void test(@ShellOption(defaultValue = "", value = {"-r", "-row", "-prefix"}, help = "指定code prefix进行测试") @Size(min = 0, max = 1) String prefix,
                     @ShellOption(defaultValue = "false", value = "--review", help = "是否是复习模式，默认false") Boolean isReview,
                     @ShellOption(defaultValue = "false", value = "--random", help = "是否随机，默认false") Boolean isRandom) throws IOException {
        Query query = new Query();
        query.setIsRemembered(isReview);
        query.setExistDefinition(true);
        query.setPrefix(prefix.toUpperCase());
        List<CommonWord> codes = codeManager.queryByCondition(query);
        if (isRandom) {
            Collections.shuffle(codes);
        }
        consoleService.outputLn("进入测试模式\n" +
                "是否测试已记住code：" + isReview + "\n" +
                "随机code：" + isRandom + "\n" +
                "测试code总数：" + codes.size());
        for (int i = 0; i < codes.size(); i++) {
            CommonWord code = codes.get(i);
            consoleService.outputLn("=========================================================================");
            consoleService.outputLn(String.format("current code:%s,please input definition.(Quit:q,Skip:Enter,Previous:p,Mark remembered:r)", code.getKey()));
            String input = consoleService.readLine();
            switch (input) {
                case "p":
                    i = i < 1 ? -1 : i - 2;
                    break;
                case "":
                    consoleService.outputLn(code.toString());
                    continue;
                case "r":
                    code.setPassTime(code.getPassTime() + 1);
                    code.setTestTime(code.getTestTime() + 1);
                    code.setRemembered(true);
                    consoleService.outputLn(code.toString());
                    codeManager.save(code);
                    continue;
                case "q":
                    consoleService.outputLn("quit test mode");
                    return;
                default:
                    code.setTestTime(code.getTestTime() + 1);
                    String source = code.getDefinition().toUpperCase();
                    String target = input.toUpperCase();
                    if (source.equals(target)) {
                        code.setPassTime(code.getPassTime() + 1);
                        consoleService.outputLn("Right answer");
                        consoleService.outputLn(code.toString());
                        codeManager.save(code);
                    } else {
                        consoleService.outputLn("Wrong answer");
                        consoleService.outputLn(code.toString());
                    }
            }
        }
    }

    @Override
    @ShellMethod(value = "Delete all under current group", key = "drop")
    public String deleteAll() {
        codeManager.clearAll();
        return "clear all data from group " + context.currentGroup;
    }

    @Override
    @ShellMethod(value = "import data using execel file", key = "import")
    public void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                           @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite) {
        String fileName = file.equals("") ? "default.xlsx" : file;
        dataImportService.importData(fileName, overwrite);
    }

}

package com.fs.tool.memory.command.impl;

import com.fs.tool.memory.command.CodeManagementCommand;
import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.model.CommonWordDO;
import com.fs.tool.memory.dao.model.WordGroupDO;
import com.fs.tool.memory.dao.query.Mode;
import com.fs.tool.memory.dao.query.Query;
import com.fs.tool.memory.dao.repository.ICodeRepository;
import com.fs.tool.memory.domain.cmd.TestCmd;
import com.fs.tool.memory.domain.service.IOService;
import com.fs.tool.memory.domain.service.WordDomainService;
import com.fs.tool.memory.service.imports.DataImportService;
import com.fs.tool.memory.service.init.DataInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.util.ArrayList;
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
    private ICodeRepository codeManager;
    @Autowired
    private DataInitService dataInitService;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private Context context;
    @Autowired
    private IOService consoleService;
    @Autowired
    private WordDomainService wordDomainService;

    @Override
    @ShellMethod(value = "init data", key = "init")
    public void initData() {
        if (codeManager.count(Query.builder().build()) > 0) {
            consoleService.outputLn("group " + context.currentGroup + " already has data!,won't init");
            return;
        }
        List<CommonWordDO> commonWords = dataInitService.biInit(context.currentGroup, DataInitService.DEFAULT_ALPHABET_LIST);
        codeManager.saveAll(commonWords);
        consoleService.outputLn("init data,current group:" + context.currentGroup);
    }

    @ShellMethod(value = "show statistic info", key = {"info", "statistic"})
    @Override
    public String statistic() {
        long total = codeManager.count(Query.builder().build());
        double hasWords = codeManager.count(Query.builder().existDefinition(true).build());
        double remembered = codeManager.count(Query.builder().isRemembered(true).build());
        List<String> groups = codeManager.groups().stream().map(WordGroupDO::getName).collect(Collectors.toList());
        String info = "当前分组:%s\n" +
                "所有分组:%s\n" +
                "联想编码总数:%d\n" +
                "有定义的联想词:%.0f,占比%.2f%%\n" +
                "已记住联想词数:%.0f,占比%.2f%%\n";
        return String.format(info, context.currentGroup, groups, total, hasWords, hasWords / total * 100, remembered, remembered / total * 100);
    }

    @Override
    @ShellMethod(value = "query word by code", key = {"query", "q"})
    public List<String> query(@ShellOption(defaultValue = ShellOption.NULL) String code,
                              @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,defult false") boolean suffix,
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
        query.setCode(code);
        if (suffix) {
            query.setCodeMode(Mode.SUFFIX);
        } else {
            query.setCodeMode(Mode.PREFIX);
        }
        return codeManager.queryByCondition(query).stream()
                .map(CommonWordDO::toString)
                .collect(Collectors.toList());
    }


    @Override
    @ShellMethod(value = "edit word", key = {"edit", "e"})
    public String edit(@Size(min = 1) String code) {
        CommonWordDO query = codeManager.queryOne(Query.builder().codeMode(Mode.EXACT).code(code.toUpperCase()).build()).orElse(null);
        String result;
        if (query == null) {
            result = "word not find";
            return result;
        }
        consoleService.outputLn(String.format("Edit word , code:%s,old definition:%s。(type q to exit)", query.getKey(), query.getDefinition()));
        String input = consoleService.readLine("please input new definition:");
        if ("q".equals(input)) {
            result = "quit edit mode";
        } else {
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
                      @ShellOption(defaultValue = ShellOption.NULL) String description) {
        String upperCode = code.toUpperCase();
        CommonWordDO oldWord = codeManager.queryOne(Query.builder().codeMode(Mode.EXACT).code(upperCode).build()).orElse(null);
        String result;
        if (oldWord != null) {
            result = "word already exist," + oldWord.toString();
            return result;
        }
        CommonWordDO commonWord = new CommonWordDO(upperCode, context.currentGroup, definition, description, false, 0, 0);
        codeManager.save(commonWord);
        return "add word success";
    }

    @Override
    @ShellMethod(value = "delete word by code", key = {"delete", "d"})
    public String delete(@ShellOption(value = {"-value", "-v"}, help = "exact match") String code,
                         @ShellOption(defaultValue = "false", value = {"-prefix", "-p"}, help = "prefix match,default false") boolean prefix,
                         @ShellOption(defaultValue = "false", value = {"-suffix", "-s"}, help = "suffix match,defult false") boolean suffix
    ) {
        Query.QueryBuilder builder = Query.builder();
        String upperCode = code.toUpperCase();
        Query query;
        if (prefix) {
            query = builder.codeMode(Mode.PREFIX).code(upperCode).build();
        } else if (suffix) {
            query = builder.codeMode(Mode.SUFFIX).code(upperCode).build();
        } else {
            query = builder.codeMode(Mode.EXACT).code(upperCode).build();
        }
        int size = codeManager.deleteByCondition(query);
        return "deleted " + size;
    }

    @Override
    @ShellMethod(value = "memory test", key = {"t", "test"})
    public void test(@ShellOption(defaultValue = "", value = {"-r", "-row", "-prefix"}, help = "code prefix match") @Size() String prefix,
                     @ShellOption(defaultValue = "false", value = "--review", help = "review word，default false") Boolean isReview,
                     @ShellOption(defaultValue = "false", value = "--random", help = "random word，default false") Boolean isRandom,
                     @ShellOption(defaultValue = "false", value = "--repeat", help = "repeat until remember at least once，default false") Boolean repeat) {
        if (wordDomainService.hasSavedTest()) {
            String out = "Detect has staging test,continue? y/n(default y)";
            String ok = consoleService.readLine(out);
            if (!ok.equals("n")) {
                wordDomainService.resume().start();
                return;
            }
        }

        Query query = new Query();
        query.setIsRemembered(isReview);
        query.setExistDefinition(true);
        query.setCodeMode(Mode.PREFIX);
        query.setCode(prefix.toUpperCase());
        List<CommonWordDO> codes = codeManager.queryByCondition(query);

        TestCmd testCmd = new TestCmd()
                .setRandom(isRandom)
                .setRepeatMode(repeat)
                .setWordDOList(codes);
        wordDomainService.createTest(testCmd).start();
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

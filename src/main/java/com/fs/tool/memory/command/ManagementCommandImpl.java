package com.fs.tool.memory.command;

import com.fs.tool.memory.command.imports.DataImportService;
import com.fs.tool.memory.command.init.DataInitService;
import com.fs.tool.memory.dao.model.CommonWord;
import com.fs.tool.memory.dao.model.WordGroup;
import com.fs.tool.memory.model.Query;
import com.fs.tool.memory.service.CodeManager;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaofushan
 * @date 2020/6/30
 */
@ShellComponent
@Slf4j
public class ManagementCommandImpl implements ManagementCommand {
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private Terminal terminal;
    @Autowired
    private LineReader reader;
    @Autowired
    private DataInitService dataInitService;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private Context context;

    private PrintWriter writer;

    @PostConstruct
    public void init() {
        writer = terminal.writer();
    }

    private void outputln(String out) {
        writer.println(out);
        writer.flush();
    }

    private void output(String out) {
        writer.print(out);
        writer.flush();
    }

    @Override
    @ShellMethod(value = "切换分组", key = {"use"})
    public void chooseGroup(@ShellOption(value = {"-g", "-group"},
            defaultValue = DEFAULT_GROUP) String group) {
        context.currentGroup = group;
        WordGroup wordGroup = new WordGroup();
        wordGroup.setName(group);
        wordGroup.setDescription(group);
        if (!codeManager.existGroup(wordGroup)) {
            codeManager.addGroup(wordGroup);
        }
        outputln("change group to " + group);
        output(statistic());
    }

    @Override
    @ShellMethod(value = "列出所有分组信息", key = {"groups"})
    public List<String> groups() {
        return codeManager.groups().stream().map(g -> g.toString()).collect(Collectors.toList());
    }

    @Override
    @ShellMethod(value = "初始化数据", key = "init")
    public void initData() {
        if (codeManager.count(Query.builder().build()) > 0) {
            outputln("group " + context.currentGroup + " already has data!,won't init");
            return;
        }
        List<CommonWord> commonWords = dataInitService.biInit(context.currentGroup, DataInitService.DEFAULT_ALPHABET_LIST);
        codeManager.saveAll(commonWords);
        outputln("init data,current group:" + context.currentGroup);
    }

    @ShellMethod(value = "显示统计信息", key = {"info", "statistic"})
    public String statistic() {
        long total = codeManager.count(Query.builder().build());
        double hasWords = codeManager.count(Query.builder().existDefinition(true).build());
        double rememebered = codeManager.count(Query.builder().isRemembered(true).build());
        String info = "当前分组:%s\n" +
                "联想编码总数:%d\n" +
                "有定义的联想词:%.0f,占比%.2f%%\n" +
                "已记住联想词数:%.0f,占比%.2f%%\n";
        return String.format(info, context.currentGroup, total, hasWords, hasWords / total * 100, rememebered, rememebered / total * 100);
    }

    @Override
    @ShellMethod(value = "查询编码,默认显示空码。", key = {"query", "q"})
    public List<String> query(@ShellOption(defaultValue = ShellOption.NULL) String code,
                              @ShellOption(value = "-e", defaultValue = "", help = "是否存在定义") Boolean existDefinition,
                              @ShellOption(value = "-r", defaultValue = "", help = "是否记住") Boolean remembered) {
        List<String> result = new ArrayList<>();
        if (!codeManager.hasCodes()) {
            result.add("没有联想数据");
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
    @ShellMethod(value = "编辑联想词", key = {"edit", "e"})
    public String edit(@Size(min = 1) String code) {
        CommonWord query = codeManager.queryOne(Query.builder().code(code.toUpperCase()).build()).orElse(null);
        String result;
        if (query == null) {
            result = "未能查询到联想词";
            return result;
        }
        outputln(String.format("编辑词组code:%s,当前联想词:%s。(输入q退出编辑)", query.getKey(), query.getDefinition()));
        String input = reader.readLine("请输入新的联想词（输入q退出编辑）:");
        switch (input) {
            case "q":
                result = "退出编辑模式";
                break;
            default:
                query.setDefinition(input.trim());
                codeManager.save(query);
                result = "联想词：" + query.getDefinition() + " 已保存";
        }
        return result;
    }

    @Override
    @ShellMethod(value = "新增联想词", key = {"add", "a"})
    public String add(@Size(min = 1) String code,
                      @ShellOption(defaultValue = ShellOption.NULL) String definition,
                      @ShellOption(defaultValue = ShellOption.NULL) String desctrption) {
        String upperCode = code.toUpperCase();
        CommonWord oldWord = codeManager.queryOne(Query.builder().code(upperCode).build()).orElse(null);
        String result;
        if (oldWord != null) {
            result = "联想词已存在," + oldWord.toString();
            return result;
        }
        CommonWord commonWord = new CommonWord(upperCode, context.currentGroup, definition, desctrption, false, 0, 0);
        codeManager.save(commonWord);
        return "新增成功";
    }

    @Override
    @ShellMethod(value = "删除联想词", key = {"delete", "d"})
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
    @ShellMethod(value = "记忆测试,记忆所有没有记住的编码", key = {"t", "test"})
    public void test(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行进行测试") @Size(min = 0, max = 1) String row,
                     @ShellOption(defaultValue = "false", value = "--review", help = "是否是复习模式，默认false") Boolean isReview,
                     @ShellOption(defaultValue = "false", value = "--random", help = "是否随机，默认false") Boolean isRandom) throws IOException {
        Query query = new Query();
        query.setIsRemembered(isReview);
        query.setExistDefinition(true);
        query.setPrefix(row.toUpperCase());
        List<CommonWord> codes = codeManager.queryByCondition(query);
        if (isRandom) {
            Collections.shuffle(codes);
        }
        outputln("进入测试模式\n" +
                "是否测试已记住code：" + isReview + "\n" +
                "随机code：" + isRandom + "\n" +
                "测试code总数：" + codes.size());
        for (int i = 0; i < codes.size(); i++) {
            CommonWord code = codes.get(i);
            outputln("=========================================================================");
            outputln(String.format("当前编码:%s,请输入联想词.(退出:q,跳过:Enter,返回上一个:p,标记为记住:r)", code.getKey()));
            String input = reader.readLine();
            switch (input) {
                case "p":
                    i = i < 1 ? -1 : i - 2;
                    break;
                case "":
                    outputln(code.toString());
                    continue;
                case "r":
                    code.setPassTime(code.getPassTime() + 1);
                    code.setTestTime(code.getTestTime() + 1);
                    code.setRemembered(true);
                    outputln(code.toString());
                    codeManager.save(code);
                    continue;
                case "q":
                    outputln("退出测试模式。");
                    return;
                default:
                    code.setTestTime(code.getTestTime() + 1);
                    String source = code.getDefinition().toUpperCase();
                    String target = input.toUpperCase();
                    if (source.equals(target)) {
                        code.setPassTime(code.getPassTime() + 1);
                        outputln("答对了!");
                        outputln(code.toString());
                        codeManager.save(code);
                    } else {
                        outputln("答错了!");
                        outputln(code.toString());
                    }
            }
        }
    }

    @Override
    @ShellMethod(value = "Delete all", key = "drop")
    public String deleteAll() {
        codeManager.clearAll();
        return "clear all data from group " + context.currentGroup;
    }

    @Override
    @ShellMethod(value = "Excel import,需遵循导入模板", key = "import")
    public void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                           @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite) {
        String fileName = file.equals("") ? "default.xlsx" : file;
        dataImportService.importData(fileName, overwrite);
    }

    @ShellMethod(value = "do nothing,only for test", key = "nothing")
    public void doNothing() {

    }
}

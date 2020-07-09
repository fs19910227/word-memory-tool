package com.fs.tool.memory.command;

import com.alibaba.excel.EasyExcel;
import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.imports.DataImportListener;
import com.fs.tool.memory.model.Query;
import com.fs.tool.memory.service.CodeManager;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    @Value("${tools.letters}")
    private List<String> letters;
    @Autowired
    private Terminal terminal;

    private PrintWriter writer;

    @Autowired
    private LineReader reader;

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
    @ShellMethod(value = "初始化数据", key = "init")
    public void initData() {
        for (int i = 0; i < letters.size(); i++) {
            String l = letters.get(i).toUpperCase();
            for (int j = 0; j < letters.size(); j++) {
                String c = letters.get(j).toUpperCase();
                if (i != j) {
                    codeManager.save(new Code(l + c, l, c, "", false, 0, 0));
                }
            }
        }
        outputln("init data");
    }

    @ShellMethod(value = "显示统计信息", key = {"info", "statistic"})
    public String statistic() {
        long total = codeManager.count(Query.builder().build());
        double hasWords = codeManager.count(Query.builder().hasWord(true).build());
        double rememebered = codeManager.count(Query.builder().isRemembered(true).build());
        String info = "联想编码总数:%d\n" +
                "已录入联想词:%.0f,占比%.1f%%\n" +
                "已记住联想词数:%.0f,占比%.1f%%\n";
        return String.format(info, total, hasWords, hasWords / total * 100, rememebered, rememebered / total * 100);
    }

    @Override
    @ShellMethod(value = "查询编码,默认不显示空码。", key = {"query", "q"})
    public List<String> query(@ShellOption(defaultValue = "") @Size(max = 2) String code,
                              @ShellOption(value = "-e", defaultValue = "true") boolean onlyExist,
                              @ShellOption(value = "-r", defaultValue = "") Boolean remembered) {
        List<String> result = new ArrayList<>();
        if (!codeManager.hasCodes()) {
            result.add("没有联想数据，请执行初始化init命令");
            return result;
        }
        code = code.toUpperCase();
        Query query = new Query();
        query.setHasWord(onlyExist);
        query.setIsRemembered(remembered);
        query.setCode(code);
        return codeManager.queryByCondition(query).stream()
                .map(info -> info.toString())
                .collect(Collectors.toList());
    }

    @Override
    @ShellMethod(value = "同步数据到数据库", key = "sync")
    public String sync() {
        codeManager.sync();
        return "同步成功！";
    }

    @Override
    @ShellMethod(value = "编辑联想词", key = {"edit", "e"})
    public String edit(@Size(min = 2, max = 2) String code) {
        Code query = codeManager.queryByCode(code.toUpperCase());
        String result;
        if (query == null) {
            result = "未能查询到联想词";
            return result;
        }
        outputln(String.format("编辑词组code:%s,当前联想词:%s。(输入q退出编辑)", query.getCode(), query.getWord()));
        String input = reader.readLine("请输入新的联想词（输入q退出编辑）:");
        switch (input) {
            case "q":
                result = "退出编辑模式";
                break;
            default:
                query.setWord(input.trim());
                codeManager.save(query);
                result = "联想词：" + query.getWord() + " 已保存";
        }
        return result;
    }

    @Override
    @ShellMethod(value = "联想词录入模式", key = {"i", "input"})
    public void typeIn(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行录入数据") @Size(min = 0, max = 1) String row,
                       @ShellOption(defaultValue = "true", value = {"-sync", "-s"}, help = "每次录入自动同步到数据库，默认为true") boolean autoSync) {
        outputln("进入词组输入模式");
        if (!codeManager.hasCodes()) {
            initData();
        }
        Iterator<Code> iterator;
        if (!StringUtils.isEmpty(row)) {
            iterator = codeManager.queryByRowIndex(row.toUpperCase()).iterator();
        } else {
            iterator = codeManager.queryAll().iterator();
        }

        while (iterator.hasNext()) {
            Code code = iterator.next();
            if (StringUtils.isEmpty(code.getWord())) {
                outputln(String.format("当前编码:%s,请输入联想词.(退出请输入:q,跳过请输入:n)", code.getCode()));
                String input = reader.readLine();
                switch (input) {
                    case "n":
                        continue;
                    case "q":
                        outputln("退出录入模式。");
                        return;
                    default:
                        code.setWord(input.trim());
                        outputln("联想词：" + code.getWord() + " 已保存");
                        if (autoSync) {
                            codeManager.save(code);
                        }
                }
            }
        }
    }

    @Override
    @ShellMethod(value = "记忆测试,记忆所有没有记住的编码", key = {"t", "test"})
    public void test(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行进行测试") @Size(min = 0, max = 1) String row,
                     @ShellOption(defaultValue = "false", value = "--review", help = "是否是复习模式，默认false") Boolean isReview,
                     @ShellOption(defaultValue = "false", value = "--random", help = "是否随机，默认false") Boolean isRandom) throws IOException {
        Query query = new Query();
        query.setIsRemembered(isReview);
        query.setHasWord(true);
        query.setCode(row.toUpperCase());
        List<Code> codes = codeManager.queryByCondition(query);
        if (isRandom) {
            Collections.shuffle(codes);
        }
        outputln("进入测试模式\n" +
                "是否测试已记住code：" + isReview + "\n" +
                "随机code：" + isRandom + "\n" +
                "测试code总数：" + codes.size());
        for (int i = 0; i < codes.size(); i++) {
            Code code = codes.get(i);
            outputln("=========================================================================");
            outputln(String.format("当前编码:%s,请输入联想词.(退出:q,跳过:Enter,返回上一个:p,标记为记住:r)", code.getCode()));
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
                    String source = code.getWord().toUpperCase();
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
    public void deleteAll() {
        codeManager.clearAll();
        outputln("清除所有数据");
    }

    @Override
    @ShellMethod(value = "Excel import,需遵循导入模板", key = "import")
    public void importData(@ShellOption(value = "-f", defaultValue = "") String file,
                           @ShellOption(value = "-o", defaultValue = "false") Boolean overwrite) {
        String fileName = file.equals("") ? "字母联想表.xlsx" : file;
        InputStream resourceAsStream = ManagementCommandImpl.class.getClassLoader().getResourceAsStream(fileName);
        EasyExcel.read(resourceAsStream, null, new DataImportListener(letters, codeManager, overwrite)).sheet().doRead();
    }

    @ShellMethod(value = "do nothing,only for test", key = "nothing")
    public void doNothing() {
        PrintWriter writer = terminal.writer();
        outputln("测试");
        String s = reader.readLine("提示");
        System.out.println(s);
    }
}

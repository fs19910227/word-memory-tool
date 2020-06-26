package com.fs.tool.memory.command;

import com.alibaba.excel.EasyExcel;
import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.model.Query;
import com.fs.tool.memory.service.CodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Size;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@ShellComponent
@Slf4j
public class ManagementCommandImpl implements ManagementCommand {
    @Autowired
    private CodeManager codeManager;
    @Value("${tools.letters}")
    private List<String> letters;

    @Override
    @ShellMethod(value = "初始化数据", key = "init")
    public void initData() {
        for (int i = 0; i < letters.size(); i++) {
            String l = letters.get(i).toUpperCase();
            for (int j = 0; j < letters.size(); j++) {
                String c = letters.get(j).toUpperCase();
                if (i != j) {
                    codeManager.save(new Code(l + c, l, c, "", false));
                }
            }
        }
        System.out.println("init data");
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
    public void sync() {
        codeManager.saveAll();
        System.out.println("同步成功！");
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
        System.out.println(String.format("编辑词组code:%s,当前联想词:%s。(输入q退出编辑)", query.getCode(), query.getWord()));
        System.out.print("请输入新的联想词（输入q退出编辑）:");
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
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
        System.out.println("进入词组输入模式");
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
                System.out.println(String.format("当前编码:%s,请输入联想词.(退出请输入:q,跳过请输入:n)", code.getCode()));
                Scanner scan = new Scanner(System.in);
                String input = scan.next();
                switch (input) {
                    case "n":
                        continue;
                    case "q":
                        System.out.println("退出录入模式。");
                        return;
                    default:
                        code.setWord(input.trim());
                        System.out.println("联想词：" + code.getWord() + " 已保存");
                        if (autoSync) {
                            codeManager.save(code);
                        }
                }
            }
        }
    }

    @Override
    @ShellMethod(value = "记忆测试,记忆所有没有记住的编码", key = {"t", "test"})
    public void test(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行进行测试") @Size(min = 0, max = 1) String row) {
        Query query = new Query();
        query.setIsRemembered(false);
        query.setHasWord(true);
        query.setCode(row.toUpperCase());
        List<Code> codes = codeManager.queryByCondition(query);
        System.out.println("简单测试模式,输入正确的单词下次不会出现");
        for (Code code : codes) {
            System.out.println(String.format("当前编码:%s,请输入联想词.(退出请输入:q,跳过请输入:n)", code.getCode()));
            Scanner scan = new Scanner(System.in);
            String input = scan.next();
            switch (input) {
                case "n":
                    continue;
                case "q":
                    System.out.println("退出记忆模式。");
                    return;
                default:
                    String source = code.getWord().toUpperCase();
                    String target = input.toUpperCase();
                    if (source.equals(target)) {
                        System.out.println("答对了，恭喜!");
                        code.setRemembered(true);
                        codeManager.save(code);
                    } else {
                        System.out.println("不好意思答错了!");
                    }
            }
        }
    }

    @Override
    @ShellMethod(value = "Delete all", key = "drop")
    public void deleteAll() {
        codeManager.clearAll();
        System.out.println("清除所有数据");
    }

    @Override
    @ShellMethod(value = "Excel import,需遵循导入模板", key = "import")
    public void importData(@ShellOption(value = "-f", defaultValue = "") String file) {
        String fileName = file.equals("") ? "字母联想表.xlsx" : file;
        InputStream resourceAsStream = ManagementCommandImpl.class.getClassLoader().getResourceAsStream(fileName);
        EasyExcel.read(resourceAsStream, null, new DataListener(letters, codeManager)).sheet().doRead();
    }
}

package com.fs.tool.memory.command;

import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.dao.repository.CodeRepository;
import com.fs.tool.memory.service.CodeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Size;
import java.util.*;

@ShellComponent
@Slf4j
public class ManagementCommand {
    @Autowired
    private CodeManager codeManager;


    @ShellMethod(value = "初始化数据", key = "init")
    public void initData() {
        String[] letters = new String[]{"a", "b", "c",
                "d", "e", "f",
                "g", "h", "i",
                "j", "k", "l",
                "w", "m", "n",
                "p", "q",
                "r", "s", "t",
                "x", "y", "z"};
        for (int i = 0; i < letters.length; i++) {
            String l = letters[i].toUpperCase();
            for (int j = 0; j < letters.length; j++) {
                String c = letters[j].toUpperCase();
                if (i != j) {
                    codeManager.save(new Code(l + c, l, c, "", false));
                }
            }
        }
        codeManager.init();
        System.out.println("init data");
    }


    @ShellMethod(value = "查询编码,默认不显示空码。", key = {"query", "q"})
    public List<String> query(@Size(min = 1, max = 2) String code, @ShellOption(value = "--e", defaultValue = "true") boolean onlyExist) {
        List<String> result = new ArrayList<>();
        if (!codeManager.hasCodes()) {
            result.add("没有联想数据，请执行初始化init命令");
            return result;
        }
        code = code.toUpperCase();
        int length = code.length();
        if (length == 1) {
            Collection<Code> codes = codeManager.queryByRowIndex(code);
            for (Code codeInfo : codes) {
                boolean empty = StringUtils.isEmpty(codeInfo.getWord());
                if (!onlyExist || !empty) {
                    result.add(codeInfo.toString());
                }
            }
        } else {
            Code codeInfo = codeManager.queryByCode(code);
            result.add(codeInfo.toString());
        }
        return result;
    }

    @ShellMethod(value = "同步数据到数据库", key = "sync")
    public void sync() {
        codeManager.saveAll();
        System.out.println("同步成功！");
    }

    @ShellMethod(value = "编辑联想词", key = {"edit", "e"})
    public String edit(@Size(min = 2, max = 2) String code) {
        Code query = codeManager.queryByCode(code);
        String result;
        if (query == null) {
            result = "未能查询到联想词";
            return result;
        }
        System.out.println(String.format("编辑词组code:%s,当前联想词:%s。(输入q退出编辑)"));
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

    @ShellMethod(value = "联想词录入模式", key = {"i", "input"})
    public void typeIn(@ShellOption(defaultValue = "", value = {"-r", "-row"}, help = "指定行录入数据") @Size(min = 1, max = 1) String row,
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

}

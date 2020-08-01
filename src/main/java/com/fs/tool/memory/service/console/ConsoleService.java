package com.fs.tool.memory.service.console;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

/**
 * 控制台操作service
 *
 * @author zhaofushan
 * @date 2020/8/2 0002 3:07
 */
@Service
public class ConsoleService implements InitializingBean {
    @Autowired
    private Terminal terminal;
    @Autowired
    private LineReader reader;

    private PrintWriter writer;

    /**
     * 读入一行数据
     *
     * @param prompt 提示信息
     */
    public String readLine(String prompt) {
        return reader.readLine(prompt);
    }

    /**
     * 读入一行数据
     */
    public String readLine() {
        return reader.readLine();
    }

    /**
     * 输出并换行
     *
     * @param out
     */
    public void outputLn(String out) {
        writer.println(out);
        writer.flush();
    }

    /**
     * 输出
     *
     * @param out
     */
    public void output(String out) {
        writer.print(out);
        writer.flush();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        writer = terminal.writer();

    }
}

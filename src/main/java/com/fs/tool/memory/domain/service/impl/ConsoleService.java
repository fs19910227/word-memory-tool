package com.fs.tool.memory.domain.service.impl;

import com.fs.tool.memory.domain.service.IOService;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 控制台操作service
 *
 * @author zhaofushan
 * @date 2020/8/2 0002 3:07
 */
@Service
public class ConsoleService implements InitializingBean, IOService {
    @Autowired
    private Terminal terminal;

    private LineReader DefaultReader;

    private PrintWriter writer;


    @Override
    public LineReader createReader() {
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    /**
     * 读入一行数据
     *
     * @param prompt 提示信息
     */
    @Override
    public String readLine(String prompt) {
        return DefaultReader.readLine(prompt);
    }

    /**
     * 读入一行数据
     */
    @Override
    public String readLine() {
        return DefaultReader.readLine();
    }

    /**
     * 读入一行数据,使用指定的reader
     *
     * @param reader
     * @param prompt 提示信息
     */
    @Override
    public String readLine(LineReader reader, String prompt) {
        return reader.readLine(prompt);
    }

    @Override
    public String readLine(LineReader reader) {
        return reader.readLine();
    }

    /**
     * 清除历史
     *
     * @param reader
     */
    @Override
    public void clearHistory(LineReader reader) {
        try {
            reader.getHistory().purge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出并换行
     *
     * @param out
     */
    @Override
    public void outputLn(String out) {
        writer.println(out);
        writer.flush();
    }

    /**
     * 输出
     *
     * @param out
     */
    @Override
    public void output(String out) {
        writer.print(out);
        writer.flush();
    }

    @Override
    public void afterPropertiesSet() {
        terminal.echo(false);
        writer = terminal.writer();
        DefaultReader = LineReaderBuilder.builder().terminal(terminal).build();
    }
}

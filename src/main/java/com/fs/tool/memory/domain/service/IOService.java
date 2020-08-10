package com.fs.tool.memory.domain.service;

import org.jline.reader.LineReader;

/**
 * io操作，接口
 * 现在为console实现
 *
 * @author zhaofushan
 * @date 2020/8/6 0006 20:26
 */
public interface IOService {

    /**
     * 创建一个新的 Reader
     */
    LineReader createReader();

    /**
     * 使用默认的reader读取数据
     *
     * @param prompt
     * @return
     */
    String readLine(String prompt);

    /**
     * 使用默认的reader读取数据
     *
     * @return
     */
    String readLine();

    /**
     * 读取一行数据
     *
     * @param reader
     * @return
     */
    String readLine(LineReader reader);

    /**
     * 读取一行数据
     *
     * @param reader
     * @param prompt
     * @return
     */
    String readLine(LineReader reader, String prompt);

    void clearHistory(LineReader reader);

    /**
     * 输出并换行
     *
     * @param out
     */
    void outputLn(String out);

    /**
     * 输出数据
     *
     * @param out
     */
    void output(String out);

}

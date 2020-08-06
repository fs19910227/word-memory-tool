package com.fs.tool.memory.domain.service;

/**
 * io操作，接口
 * 现在为console实现
 *
 * @author zhaofushan
 * @date 2020/8/6 0006 20:26
 */
public interface IOService {

    String readLine(String prompt);

    String readLine();

    void outputLn(String out);

    void output(String out);
}

package com.fs.tool.memory.domain.service;

import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.domain.bo.WordTestBO;
import com.fs.tool.memory.domain.cmd.TestCmd;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 单词领域服务
 *
 * @author zhaofushan
 * @date 2020/8/6 0006 20:19
 */
@Service
public class WordDomainService implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Autowired
    private Context context;

    /**
     * 创建新测试
     *
     * @param testCmd
     * @return
     */
    public WordTestBO createTest(TestCmd testCmd) {
        context.savedTestMap.remove(context.currentGroup);
        return applicationContext.getBean(WordTestBO.class)
                .init(testCmd);
    }

    /**
     * 恢复之前的测试
     *
     * @return
     */
    public WordTestBO resume() {
        return context.savedTestMap.get(context.currentGroup);
    }

    /**
     * 是否有暂存的测试
     *
     * @return
     */
    public boolean hasSavedTest() {
        return context.savedTestMap.containsKey(context.currentGroup);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

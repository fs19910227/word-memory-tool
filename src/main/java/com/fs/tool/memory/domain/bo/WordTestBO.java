package com.fs.tool.memory.domain.bo;

import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.model.CommonWordDO;
import com.fs.tool.memory.dao.repository.ICodeRepository;
import com.fs.tool.memory.domain.cmd.TestCmd;
import com.fs.tool.memory.domain.enums.TestStatus;
import com.fs.tool.memory.domain.service.IOService;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author zhaofushan
 * @date 2020/8/6 0006 20:33
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class WordTestBO {

    @Autowired
    private ICodeRepository codeRepository;
    @Autowired
    private IOService consoleService;
    @Autowired
    private Context context;
    private int currentIndex = 0;

    private Set<CommonWordDO> rememberedWords = new HashSet<>();

    private List<CommonWordDO> words;

    private boolean repeat;
    private boolean isRandom;
    private TestStatus testStatus;
    private int repeatTimes;
    private LineReader reader;

    /**
     * 上一个
     */
    private void doPrevious() {
        currentIndex -= 2;
        currentIndex = Math.max(currentIndex, -1);
    }

    /**
     * 跳过
     *
     * @param code
     */
    private void doSkip(CommonWordDO code) {
        code.setTestTime(code.getTestTime() + 1);
        codeRepository.save(code);
        consoleService.outputLn(code.toString());
    }

    /**
     * 标记为记住
     */
    private void markRemember(CommonWordDO code) {
        code.setPassTime(code.getPassTime() + 1);
        code.setTestTime(code.getTestTime() + 1);
        code.setRemembered(true);
        consoleService.outputLn(code.toString());
        codeRepository.save(code);
    }


    /**
     * 判断答案
     *
     * @return right->true wrong ->false
     */
    private boolean judgeAnswer(CommonWordDO code, String input) {
        code.setTestTime(code.getTestTime() + 1);
        String source = code.getDefinition().toUpperCase();
        if (source.equals(input)) {
            code.setPassTime(code.getPassTime() + 1);
            consoleService.outputLn("Right answer");
            consoleService.outputLn(code.toString());
            codeRepository.save(code);
            return true;
        } else {
            consoleService.outputLn("Wrong answer");
            consoleService.outputLn(code.toString());
        }
        return false;
    }

    /**
     * 暂存测试状态
     */
    private void pause() {
        consoleService.outputLn("save and quit test mode");
        testStatus = TestStatus.PAUSE;
    }

    private void test(CommonWordDO code) {
        consoleService.outputLn("=========================================================================");
        consoleService.outputLn(String.format("current code:%s,please input definition.(Quit:q,Skip:Enter,Previous:p,Mark remembered:r)", code.getKey()));
        String input = consoleService.readLine(reader).toLowerCase();
        switch (input) {
            case "p":
                doPrevious();
                break;
            case "":
                doSkip(code);
                break;
            case "q":
                pause();
                break;
            case "r":
                markRemember(code);
                break;
            default:
                if (judgeAnswer(code, input.toUpperCase())) {
                    rememberedWords.add(code);
                }

        }
    }

    /**
     * 初始化cmd
     *
     * @param cmd
     * @return
     */
    public WordTestBO init(TestCmd cmd) {
        this.words = cmd.getWordDOList();
        this.repeat = cmd.isRepeatMode();
        this.isRandom = cmd.isRandom();
        this.rememberedWords.clear();
        this.currentIndex = 0;
        this.repeatTimes = 0;
        this.reader = consoleService.createReader();
        testStatus = TestStatus.IDLE;
        if (isRandom) {
            Collections.shuffle(words);
        }

        return this;
    }

    /**
     * 开始测试
     */
    public void start() {
        consoleService.outputLn("ENTER TEST MODE\n" +
                "random " + isRandom + "\n" +
                "repeat " + repeat + "\n" +
                "total codes：" + words.size() + "\n" +
                "remembered codes: " + rememberedWords.size()
        );
        testStatus = TestStatus.RUNNING;
        boolean needRepeat = repeat;
        do {
            for (; currentIndex < words.size(); currentIndex++) {
                CommonWordDO code = words.get(currentIndex);
                if (rememberedWords.contains(code)) {
                    continue;
                }
                test(code);
                if (testStatus == TestStatus.IDLE) {
                    context.savedTestMap.remove(context.currentGroup);
                    return;
                }
                if (testStatus == TestStatus.PAUSE) {
                    context.savedTestMap.put(context.currentGroup, this);
                    return;
                }
            }
            int remembered = rememberedWords.size();
            int rest = words.size() - remembered;
            String out = String.format("Test complete %d cycle.remembered:%d,rest:%d", ++repeatTimes, remembered, rest);
            consoleService.outputLn(out);
            if (rest == 0) {
                needRepeat = false;
            }
            //reset
            currentIndex = 0;
        } while (needRepeat);
    }
}

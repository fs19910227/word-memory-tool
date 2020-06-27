package com.fs.tool.memory.command;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.service.CodeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * excel导入监听
 */
public class DataListener extends AnalysisEventListener {
    private final CodeManager codeManager;
    private final List<String> letterIndex;
    private List<Code> result = new ArrayList<>();

    public DataListener(List<String> letters, CodeManager codeManager) {
        this.letterIndex = letters;
        this.codeManager = codeManager;
    }

    @Override
    public void invoke(Object data, AnalysisContext context) {
        Map<String, String> map = (Map<String, String>) data;
        List<String> values = new ArrayList<>(map.values());
        String second = values.get(0).toUpperCase();
        for (int i = 0; i < letterIndex.size(); i++) {
            String first = letterIndex.get(i).toUpperCase();
            String code = first + second;
            String word = values.get(i + 1);
            if (word == null || word.equals("\\")) {
                continue;
            }
            result.add(new Code(code, first, second, word, false, 0, 0));
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        for (Code code : result) {
            Code old = codeManager.queryByCode(code.getCode());
            if (old != null) {
                code.setRemembered(old.isRemembered());
            }
            codeManager.save(code);
        }
        System.out.println("导入成功");
    }
}

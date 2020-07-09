package com.fs.tool.memory.imports;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.service.CodeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * excel导入监听
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
public class DataImportListener extends AnalysisEventListener {
    private final CodeManager codeManager;
    private final List<String> letterIndex;
    private final boolean overwrite;
    private List<Code> result = new ArrayList<>();

    /**
     * @param letters     字母列表
     * @param codeManager manager
     * @param overwrite   是否覆盖
     */
    public DataImportListener(List<String> letters,
                              CodeManager codeManager,
                              boolean overwrite) {
        this.letterIndex = letters;
        this.codeManager = codeManager;
        this.overwrite = overwrite;
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
            codeManager.save(code, overwrite);
        }
    }
}

package com.fs.tool.memory.imports;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fs.tool.memory.command.Context;
import com.fs.tool.memory.dao.model.CommonWord;
import com.fs.tool.memory.service.CodeManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 二维 excel导入监听
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
public class DataImportListener extends AnalysisEventListener {
    private final CodeManager codeManager;
    private final List<String> heads = new ArrayList<>();
    private final boolean overwrite;
    private final Context context;
    private List<CommonWord> result = new ArrayList<>();

    /**
     * @param codeManager manager
     * @param context
     * @param overwrite   是否覆盖
     */
    public DataImportListener(
            CodeManager codeManager,
            Context context, boolean overwrite) {
        this.codeManager = codeManager;
        this.overwrite = overwrite;
        this.context = context;
    }

    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        Iterator iterator = headMap.values().iterator();
        iterator.next();
        while (iterator.hasNext()) {
            Object head = iterator.next();
            heads.add(head.toString());
        }
    }

    @Override
    public void invoke(Object data, AnalysisContext context) {
        Map<String, String> map = (Map<String, String>) data;
        List<String> values = new ArrayList<>(map.values());
        String second = values.get(0).toUpperCase();
        for (int i = 0; i < heads.size(); i++) {
            String first = heads.get(i).toUpperCase();
            String word = values.get(i + 1);

            if (word == null || word.equals("\\")) {
                continue;
            }
            String currentGroup = this.context.currentGroup;
            CommonWord aDefault = new CommonWord(first + second, currentGroup, word, "", false, 0, 0);
            result.add(aDefault);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        for (CommonWord code : result) {
            codeManager.save(code, overwrite);
        }
    }
}

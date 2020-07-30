package com.fs.tool.memory.command.init;

import com.fs.tool.memory.dao.model.CommonWord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化服务
 *
 * @author zhaofushan
 * @date 2020/7/30 0030 20:58
 */
@Service
public class DataInitService {
    public static final List<String> DEFAULT_ALPHABET_LIST = new ArrayList<>();
    public static final List<String> DEFAULT_NUMBER_LIST = new ArrayList<>();

    static {
        for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            DEFAULT_ALPHABET_LIST.add(c + "");
        }
        for (char c : "0123456789".toCharArray()) {
            DEFAULT_NUMBER_LIST.add(c + "");
        }
    }

    /**
     * 二维word初始化
     *
     * @param codeMetaList 码源列表
     * @param group        编码分组
     * @return
     */
    public List<CommonWord> biInit(String group, List<String> codeMetaList) {
        List<CommonWord> resultList = new ArrayList<>();
        for (int i = 0; i < codeMetaList.size(); i++) {
            String l = codeMetaList.get(i).toUpperCase();
            for (int j = 0; j < codeMetaList.size(); j++) {
                String c = codeMetaList.get(j).toUpperCase();
                CommonWord commonWord = new CommonWord(l + c, group, null, null, false, 0, 0);
                resultList.add(commonWord);
            }
        }
        return resultList;
    }

    /**
     * 线性word初始化
     *
     * @param codeMetaList 码源列表
     * @param group        编码分组
     * @return
     */
    public List<CommonWord> linearWordInit(String group, List<String> codeMetaList) {
        List<CommonWord> resultList = new ArrayList<>();
        for (String s : codeMetaList) {
            String l = s.toUpperCase();
            CommonWord commonWord = new CommonWord(l, group, null, null, false, 0, 0);
            resultList.add(commonWord);
        }
        return resultList;
    }
}

package com.fs.tool.memory.service.imports;

import com.alibaba.excel.EasyExcel;
import com.fs.tool.memory.core.Context;
import com.fs.tool.memory.dao.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * 数据导入服务
 *
 * @author zhaofushan
 * @date 2020/7/30 0030 20:58
 */
@Service
public class DataImportService {
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private Context context;

    public void importData(String fileName, boolean overwrite) {
        InputStream resourceAsStream = DataImportService.class.getClassLoader().getResourceAsStream(fileName);
        EasyExcel.read(resourceAsStream, null, new DataImportListener(codeRepository, context, overwrite)).sheet().doRead();
    }
}

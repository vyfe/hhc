package com.vyfe.hhc.decoder.utils;

import java.io.File;
import java.util.List;

import com.vyfe.hhc.poker.SessionMsg;
import com.vyfe.hhc.system.HhcException;
import org.apache.commons.lang3.tuple.Pair;

public interface FileParser {
    /**
     * 导出文件，文本分拆成每手一组并输出文件md5
     * @param file 对应的File对象
     * @return
     */
    Pair<List<List<String>>, String> parseHandsFile(File file) throws HhcException;
    
    SessionMsg parseTournamentOverviewFile(File file) throws HhcException;
}

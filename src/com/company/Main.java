package com.company;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import net.sf.json.JSONObject;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    static final int pageSize = 1000;
    static int startPage = 0;
    static HashMap<String,HashSet<String>> json=new HashMap<>();
    public static void main(String[] args) {
        for (int offset = 0; offset < 1000; offset++) {
            long t = System.currentTimeMillis();
            String[] text = Search(offset * pageSize);
            for (int j = 0; j < text.length; j++) {
                if (text[j] == null || text[j].length() == 0) {
                    continue;
                }
                CoNLLSentence sentence = HanLP.parseDependency(text[j]);
                CoNLLWord[] wordArray = sentence.getWordArray();
                for (int i = wordArray.length - 1; i >= 0; i--) {
                    CoNLLWord word = wordArray[i];
                    if (word.DEPREL.equals("主谓关系")) {
                        if (!json.containsKey(word.LEMMA)){
                            json.put(word.LEMMA,new HashSet<String>());
                        }
                        json.get(word.LEMMA).add(word.HEAD.LEMMA);
                    }
                }
            }
            System.out.printf("step %d/1000 finished %d ms\n", offset,System.currentTimeMillis()-t);
        }
        System.out.println("分析介绍");
        System.out.println("开始写入文件");
        JSONObject j=new JSONObject();
        j.putAll(json);
        System.out.println();
        writeToFile("1.json",j);
        System.out.println("写入文件完成");
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
    }

    public static void writeToFile(String file, Object conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false)));
            out.write(conent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] Search(int offset) {
        String getrow = "SELECT \"text\" FROM meituan_comments limit " + Integer.toString(pageSize) +
                " offset " + Integer.toString(startPage + offset);
        ResultSet rs = Databaseoperator.DoGetResultSql(getrow);
        int rownum = pageSize;
        String resultArray[] = new String[rownum];
        int count = 0;
        try {
            while (rs.next()) {
                String abbs;
                abbs = rs.getString(1);
                resultArray[count] = abbs;
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultArray;
    }

}

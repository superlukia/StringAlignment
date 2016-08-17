/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringalignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.biojava.bio.BioException;
import org.biojava.bio.BioRuntimeException;
import org.biojava.bio.alignment.NeedlemanWunsch;
import org.biojava.bio.alignment.SmithWaterman;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.io.CharacterTokenization;
import org.biojava.bio.symbol.AlphabetManager;
import org.biojava.bio.symbol.AtomicSymbol;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleAlphabet;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

/**
 *
 * @author superlukia
 */
public class StringAlignment {

//    private static int minline = 10000;
//    private static String minalign = null;

    public static void main(String[] args) throws IllegalSymbolException, IOException, BioException, InterruptedException {
        //带比对的两个文本文件a.txt:query, b.txt:target
        String s1 = readfile(new File("a.txt"));
        String s2 = readfile(new File("b.txt"));

        //配置Alphabet,CharacterTokenization
        HashMap<String, AtomicSymbol> symbolmap = new HashMap<>();
        addstr2map(s1, symbolmap);
        addstr2map(s2, symbolmap);
        SimpleAlphabet alphabet = new SimpleAlphabet("stringalphabet");
        for (AtomicSymbol s : symbolmap.values()) {
            alphabet.addSymbol(s);
        }
        CharacterTokenization token = new CharacterTokenization(alphabet, true);
        for (String s : symbolmap.keySet()) {
            char c = s.charAt(0);
            token.bindSymbol(symbolmap.get(s), c);
        }
        alphabet.putTokenization("token", token);
        //
        
        //选取m：match,p:penalty=mismatch,insert,delete,pe:gapExtend,进行比对
        for (short m = -3; m < -2; m++) {
            for (short p = 1; p < 2; p++) {
                for (short pe = 1; pe < 2; pe++) {
                    short match = m;
                    short penalty = p;
                    short replace = penalty;
                    short insert = penalty;
                    short delete = penalty;
                    short gapExtend = pe;
                    //重复10次
                    int roundnum=10;
                    for (int i = 0; i < roundnum; i++) {
                        NeedlemanWunsch sw = align(match, replace, alphabet, insert, delete, gapExtend, s1, symbolmap, s2);
                        String as = sw.getAlignmentString();
                        //格式化输出比对结果（非ascii字符自身宽度为2，将ascii字符宽度从1补充空格成为2）
                        //并返回比对的位置类，用于将这段抠出来，将剩余字符串继续下一轮比对
                        AlignmentPos ap = ResultFormatter.outputformat(new BufferedReader(new StringReader(as)));
                        System.out.println();
                        System.out.println(ap);
                        System.out.println();
                        s1=exclusestr(s1, ap.getQstart(), ap.getQend());
                        s2=exclusestr(s2, ap.getSstart(), ap.getSend());
                    }

                }
            }
        }
    }
    
    /**
     * 比对并返回NeedlemanWunsch对象(SmithWaterman的父类，使用局部比对与全局比对算法进行过比较，视情况不同选择)
     * @param match
     * @param replace
     * @param alphabet
     * @param insert
     * @param delete
     * @param gapExtend
     * @param s1    query
     * @param symbolmap  遗留参数，用来从string生成SymbolList，其实可以用Tokenization.parseStream来代替
     * @param s2    target
     * @return
     * @throws BioRuntimeException
     * @throws IllegalSymbolException
     * @throws BioException 
     */
    private static NeedlemanWunsch align(short match, short replace, SimpleAlphabet alphabet, short insert, short delete, short gapExtend, String s1, HashMap<String, AtomicSymbol> symbolmap, String s2) throws BioRuntimeException, IllegalSymbolException, BioException {
        short mmatch = (short) -match;
        short mreplace = (short) -replace;
        SubstitutionMatrix matrix = new SubstitutionMatrix(alphabet, mmatch, mreplace);
        NeedlemanWunsch sw = new SmithWaterman(match, replace, insert, delete, gapExtend, matrix);
        SymbolList sl1 = createlist(s1, symbolmap, alphabet);
        SymbolList sl2 = createlist(s2, symbolmap, alphabet);
        int pa = sw.pairwiseAlignment(sl1, sl2);
        return sw;
    }
//    private static int count = 0;
//    private static synchronized void compare(String astr) {
//        int ln = astr.split("\\n").length;
//        if (ln < minline) {
//            minline = ln;
//            minalign = astr;
//            System.out.println(ln);
//        }
//        count++;
//        System.out.println(count);
//
//    }

    /**
     * 将s1添加到映射symbolmap
     * @param s1 需要添加的字符（串）
     * @param symbolmap 用于保存从字符（串）到Symbol的映射
     */
    private static void addstr2map(String s1, HashMap<String, AtomicSymbol> symbolmap) {
        for (int i = 0; i < s1.length(); i++) {
            String substr = s1.substring(i, i + 1);
            if (symbolmap.containsKey(substr)) {
                continue;
            } else {
                AtomicSymbol s = AlphabetManager.createSymbol(substr);
                symbolmap.put(substr, s);
            }
        }
    }
    
    /**
     * 读取GB18030编码文本文件(word转出来的，汗，非utf8反人类)
     * @param file
     * @return
     * @throws IOException 
     */
    private static String readfile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB18030"));
        String line = "";
        while ((line = br.readLine()) != null) {
            line = line.replaceAll("[\\s　]", "");
            sb.append(line);
        }
        return sb.toString();
    }
    
    /**
     * 将整个需要比对的字符串转换为后续输入SymbolList
     * @param s
     * @param symbolmap
     * @param alphabet
     * @return
     * @throws IllegalSymbolException 
     */
    private static SymbolList createlist(String s, HashMap<String, AtomicSymbol> symbolmap, SimpleAlphabet alphabet) throws IllegalSymbolException {

        List<Symbol> ls = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            String substr = s.substring(i, i + 1);
            AtomicSymbol symbol = symbolmap.get(substr);
            ls.add(symbol);
        }
        SimpleSymbolList result = new SimpleSymbolList(alphabet, ls);
        return result;
    }
    
    /**
     * 因为一次比对只能找到局部比对最佳的一部分，采取每次比对将上一次比对最佳的一部分抠出来
     * 比对剩余继续寻找局部最佳
     * @param str 需要抠的字符串
     * @param start 从1开始，采用比对结果中显示的值
     * @param end 同start
     * @return 
     */
    private static String exclusestr(String str, int start, int end) {
//        System.out.println(str.substring(start-1, end));
        return str.substring(0, start - 1) + str.substring(end);
    }
}

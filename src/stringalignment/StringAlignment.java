/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringalignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.bio.BioException;
import org.biojava.bio.BioRuntimeException;
import org.biojava.bio.alignment.NeedlemanWunsch;
import org.biojava.bio.alignment.SmithWaterman;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.io.CharacterTokenization;
import org.biojava.bio.seq.io.NameTokenization;
import org.biojava.bio.symbol.Alignment;
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

    private static int minline = 10000;
    private static String minalign = null;

    public static void main(String[] args) throws IllegalSymbolException, IOException, BioException, InterruptedException {

        String s1 = readfile(new File("a.txt"));
        String s2 = readfile(new File("b.txt"));
//        s1=exclusestr(s1, 11137, 11691);
//        s2=exclusestr(s2, 9014, 9631);
//        s1=exclusestr(s1, 6415, 6602);
//        s2=exclusestr(s2, 5821, 6002);
//        s1=exclusestr(s1, 13537, 13610);

//        System.out.println(s1);
//        System.out.println(s2);
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

        ExecutorService tp = Executors.newFixedThreadPool(4);
        for (short m = -3; m < -2; m++) {
            for (short p = 1; p < 2; p++) {
                for (short pe = 1; pe < 2; pe++) {
                    short match = m;
                    short penalty = p;
                    short replace = penalty;
                    short insert = penalty;
                    short delete = penalty;
                    short gapExtend = pe;
                    for (int i = 0; i < 10; i++) {
                        NeedlemanWunsch sw = align(match, replace, alphabet, insert, delete, gapExtend, s1, symbolmap, s2);
                        String as = sw.getAlignmentString();
                        AlignmentPos ap = ResultFormatter.outputformat(new BufferedReader(new StringReader(as)));
                        System.out.println();
                        System.out.println(ap);
                        System.out.println();
                        s1=exclusestr(s1, ap.getQstart(), ap.getQend());
                        s2=exclusestr(s2, ap.getSstart(), ap.getSend());
                    }

//        tp.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        align(match, replace, alphabet, insert, delete, gapExtend, s1, symbolmap, s2);
//                    } catch (Exception ex) {
//                        Logger.getLogger(StringAlignment.class.getName()).log(Level.SEVERE, null, ex);
//                        System.err.println(minline);
//                        System.err.println(minalign);
//                    }
//                }
//            });
                }
            }
        }
//        tp.awaitTermination(3600, TimeUnit.SECONDS);
//        System.out.println(minline);
//        ResultFormatter.outputformat(new BufferedReader(new StringReader(minalign)));
        System.exit(0);
    }

    private static NeedlemanWunsch align(short match, short replace, SimpleAlphabet alphabet, short insert, short delete, short gapExtend, String s1, HashMap<String, AtomicSymbol> symbolmap, String s2) throws BioRuntimeException, IllegalSymbolException, BioException {
        short mmatch = (short) -match;
        short mreplace = (short) -replace;
        SubstitutionMatrix matrix = new SubstitutionMatrix(alphabet, mmatch, mreplace);
        NeedlemanWunsch sw = new SmithWaterman(match, replace, insert, delete, gapExtend, matrix);
        SymbolList sl1 = createlist(s1, symbolmap, alphabet);
        SymbolList sl2 = createlist(s2, symbolmap, alphabet);
        int pa = sw.pairwiseAlignment(sl1, sl2);
        return sw;
//        try {
//            Alignment alignment = sw.getAlignment(sl1, sl2);
//            
//            SymbolList l = alignment.symbolListForLabel("query");
//            l.seqString();
//        } catch (Exception ex) {
//            Logger.getLogger(StringAlignment.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        System.out.print(""+mmatch+" "+mreplace+" "+gapExtend+" ");
//        compare(sw.getAlignmentString());
//        System.out.println(sw.getAlignmentString().split("\\n").length);
//        System.out.println(pa);
//        System.out.println(sw.getAlignmentString());
    }
    private static int count = 0;

    private static synchronized void compare(String astr) {
        int ln = astr.split("\\n").length;
        if (ln < minline) {
            minline = ln;
            minalign = astr;
            System.out.println(ln);
        }
        count++;
        System.out.println(count);

    }

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
     * @param str
     * @param start
     * @param end
     * @return 
     */
    private static String exclusestr(String str, int start, int end) {
//        System.out.println(str.substring(start-1, end));
        return str.substring(0, start - 1) + str.substring(end);
    }
}

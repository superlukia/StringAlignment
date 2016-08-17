/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stringalignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author superlukia
 */
public class ResultFormatter {
    public static void main(String[] args) throws IOException {
        String test1="Query:	   172 86页（3）《瑞金医院人事处工作报告》，现存瑞金医院档案室1993-人事处-37(长期)-第8页（4）《博士研究生导师介 228 ";
        String test2="Target:	    76 单薄，要写血研所和临床的合作，----侧重临床科研这方面，-----特别是转化方面的工作，不写说不过去。血液科----- 121 ";
        Matcher m1=startp.matcher(test1);
        if(m1.find()){
            System.out.println(m1.group(1));
        }
        m1=endp.matcher(test1);
        if(m1.find()){
            System.out.println(m1.group(1));
        }
        m1=startp.matcher(test2);
        if(m1.find()){
            System.out.println(m1.group(1));
        }
        m1=endp.matcher(test2);
        if(m1.find()){
            System.out.println(m1.group(1));
        }
    }
    /**
     * 格式化输出比对结果（非ascii字符自身宽度为2，将ascii字符宽度从1补充空格成为2）
     * 并返回比对的位置类，用于将这段抠出来，将剩余字符串继续下一轮比对
     * @param br
     * @return 
     */
    public static AlignmentPos outputformat(BufferedReader br) {
        AlignmentPos result=new AlignmentPos();
        String line="";
        int count=0;
        boolean isstart=false;
        try{
            while((line=br.readLine())!=null){
                if(!isstart){
                    if(line.startsWith("Query")){
                        isstart=true;
                    }else{
                        continue;
                    }
                }
                count++;
                if(line.isEmpty()) continue;
                if(count % 4 == 1){
                    System.out.println(addspace(line,14));
                    updateq(result,line);
                }
                else if(count % 4 ==2) System.out.println(addspace(line,15));
                else if(count %4==3) {
                    System.out.println(addspace(line,15));
                    updates(result,line);
                }
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private static String addspace(String str){
        return addspace(str,0);
    }
    private static String addspace(String str,int start){
        StringBuilder sb=new StringBuilder();
        sb.append(str.substring(0, start));
        for(int i=start;i<str.length();i++){
            char c=str.charAt(i);
            if(c<128){
                sb.append(c).append(' ');
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    private static Pattern startp=Pattern.compile("(?:Query:|Target:)\\s+(\\d+)"),endp=Pattern.compile("(\\d+)\\s*$");
    private static void updateq(AlignmentPos result,String line) {
        Matcher s=startp.matcher(line);
        s.find();
        Matcher e=endp.matcher(line);
        e.find();
        if(result.getQstart()==0){
            result.setQstart(Integer.parseInt(s.group(1)));
        }
        result.setQend(Integer.parseInt(e.group(1)));
    }
    private static void updates(AlignmentPos result,String line) {
        Matcher s=startp.matcher(line);
        s.find();
        Matcher e=endp.matcher(line);
        e.find();
        if(result.getSstart()==0){
            result.setSstart(Integer.parseInt(s.group(1)));
        }
        result.setSend(Integer.parseInt(e.group(1)));
    }
}

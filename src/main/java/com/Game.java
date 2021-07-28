package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Game {
    public static void main(String[] args) throws IOException {

        System.out.println("Please enter the results of this round");
        InputStreamReader is = new InputStreamReader(System.in); //new构造InputStreamReader对象
        BufferedReader br = new BufferedReader(is); //拿构造的方法传到BufferedReader中，此时获取到的就是整个缓存流

        //接受控制台输入的保龄球积分字符串,并去除空格
        String originalInput = br.readLine();
        String input = originalInput.replace(" ","");

        //进行第一次验证，判断得分语句是否合规，不合规则打印-1，退出程序
        if (firstJudge(input) == -1){
            System.out.println("-1");
            System.exit(0);
        }

        //计算选手的总局数
        int framesNum = countFrames(input);

        //生成strike局数的索引
        List<Integer> strikeIndexes = allStrike(input);

        //分割出非strike局，存储到数组中
        String[] noStrike = input.split("X");

        //生成所有球局的得分数组
        List<String> frames = splitAllFrames(noStrike,strikeIndexes,framesNum);

        //进行第二次验证，判断记录过程之中是否合法
        if (secondJudge(frames) == -1){
            System.out.println("-1");
            System.exit(0);
        }
        //计算本场的得分
        int scores = addScores(frames);

        System.out.println(scores);
    }

    public static int secondJudge(List<String> frames){
        int status = 0;
        int count = 0;
        for (String i : frames){
            //判断有无非strike局，得分字段仅含有一个字符的情况
            if (!i.contains("X") && i.length() == 1 && count != frames.size() - 1){
                status = -1;
                break;
            }
            char[] frame = i.toCharArray();
            //判断是否有第一次投掷是strike时，依旧有第二次投掷的情况
            if (i.length()>1 && i.contains("X")){
                status = -1;
                break;
            }

            //判断是否有第二次投掷已经全部投倒，但是没有记录成spare的情况
            if (i.length() > 1 && !i.contains("-")  && !i.contains("/") && (Integer.parseInt(String.valueOf(frame[0])) + Integer.parseInt(String.valueOf(frame[1])) >= 10)){
                status = -1;
                break;
            }

            //判断是否有将strike记录成spare的情况
            if (frame[0] == '/'){
                status = -1;
                break;
            }

            //判断是否正确含有奖励回合
            if (count == 9){
                if (i.contains("/") && frames.size() != 11){
                    status = -1;
                    break;
                }else if (i.contains("X")){
                    if (frames.size() >12){
                        status = -1;
                        break;
                    }
                    if (!frames.get(10).contains("X")){
                        if (frames.size() == 11){
                            if (frames.get(10).contains("X")){
                                status = -1;
                                break;
                            }
                        }else if (frames.size() == 12 && frames.get(11).length() == 1){
                            status = -1;
                            break;

                        }
                    }
                    if (frames.get(10).contains("X") && frames.size() == 11){
                        status = -1;
                        break;
                    }

                }else if (!i.contains("X") && !i.contains("/") && frames.size() != 10){
                    status = -1;
                }
            }
            count = count + 1;
        }

        return status;
    }

    public static int firstJudge(String scoreString){
        int status = 0;

        //验证字符串是否长度合法
        if (scoreString.length() < 11 || scoreString.length() > 23){
            status = -1;
        }

        //验证字符串是否包含0
        if (scoreString.contains("0")){
            status = -1;
        }

        //验证字符串是否含有小写字母，或者含有除X之外的大写字母
        if (scoreString.matches(".*[a-zA-WY-Z]+.*")){
            status = -1;
        }

        //验证字符串是否含有除/ - 之外的特殊字符
        if (scoreString.matches(".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]+.*")){
            status = -1;
        }

        return status;
    }
    public static List<String> splitAllFrames(String[] arr1,List<Integer> strikeIndexes,int framesNum){

        List<String> frames = new ArrayList<String>();

        int count = 0;
        int strikeCount = 0;

        //若选手全为strike局数，则直接生成积分数组
        if (arr1.length == 0){
            for (int i : strikeIndexes){
                frames.add("X");
            }
            return frames;
        }

        //这里的算法还有改进的空间，仅仅是实现功能，稍显累赘，可进行封装
        while (count < framesNum){
            //存储首部的strike局
            if (strikeCount < strikeIndexes.size()){
                if (count == strikeIndexes.get(strikeCount)){
                    frames.add("X");
                    count = count + 1;
                    strikeCount = strikeCount + 1;
                    if (strikeCount < strikeIndexes.size()){
                        while ((strikeIndexes.get(strikeCount) - strikeIndexes.get(strikeCount - 1)) == 1){
                            frames.add("X");
                            count = count + 1;
                            strikeCount = strikeCount + 1;
                            if (strikeCount == strikeIndexes.size()){
                                break;
                            }
                        }
                    }
                }
            }
            //对存储的非strike局进行遍历，分割字符串保存到积分数组
            for (String i : arr1) {
                //在对每个字段进行处理前，检查中间是否有strike局，有的话进行存储
                if (strikeCount < strikeIndexes.size()){
                    if (count == strikeIndexes.get(strikeCount)){
                        frames.add("X");
                        count = count + 1;
                        strikeCount = strikeCount + 1;
                        if (strikeCount < strikeIndexes.size()){
                            while ((strikeIndexes.get(strikeCount) - strikeIndexes.get(strikeCount - 1)) == 1){
                                frames.add("X");
                                count = count + 1;
                                strikeCount = strikeCount + 1;
                                if (strikeCount == strikeIndexes.size()){
                                    break;
                                }
                            }
                        }
                    }
                }
                List<String> arr2 = splitNoStrike(i);
                //对每个字段进行分割，存储
                for (String j : arr2) {
                    frames.add(j);
                    count = count + 1;
                }
                //在尾部进行存储strike局数
                if (strikeCount < strikeIndexes.size()){
                    if (count == strikeIndexes.get(strikeCount)){
                        frames.add("X");
                        count = count + 1;
                        strikeCount = strikeCount + 1;
                        if (strikeCount < strikeIndexes.size()){
                            while ((strikeIndexes.get(strikeCount) - strikeIndexes.get(strikeCount - 1)) == 1){
                                frames.add("X");
                                count = count + 1;
                                strikeCount = strikeCount + 1;
                                if (strikeCount == strikeIndexes.size()){
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return frames;
    }

    //对每个字段进行处理，分割成单局的积分数组
    public static List<String> splitNoStrike(String string){
        char[] chars = string.toCharArray();
        int charsLength = chars.length;
        List<String> result = new ArrayList<String>();
        int count = 0;
        while (count < charsLength/2){
            result.add(chars[2*count] + "" + chars[2*count + 1]);
            count = count + 1;
        }

        if (2*(count + 1) == (charsLength + 1)){
            result.add("" + chars[charsLength-1]);
        }

        return result;
    }

    //返回总局数
    public static int countFrames(String scoreString){
        String notStrike = scoreString.replace("X","");
        int strikeNum = scoreString.length() - notStrike.length();
        return (notStrike.length() + 1)/2 + strikeNum;//代表非strike局的字符除以2 + strike局数 = 总局数
    }

    //根据规则计算积分
    public static int addScores(List<String> frames){
        int scores = 0;
        for (int i=0;i<10;i++){//遍历局数仅为10局，奖励局不进行遍历
            if (frames.get(i).contains("X")){
                scores = scores + strikeScore(frames,i);
            }else if (frames.get(i).contains("/")){
                scores = scores + spareScores(frames,i);
            }else {
                char[] frame = frames.get(i).toCharArray();
                for (int j = 0;j<frame.length;j++){
                    if (frame[j] != '-'){
                        scores = scores + Integer.parseInt(String.valueOf(frame[j]));
                    }
                }
            }
        }

        return scores;
    }

    //strike局的积分计算
    public static int strikeScore(List<String> frame,int index){
        int strikeScores = 10;
        String nextFrame = frame.get(index + 1);
        if (nextFrame.contains("X")){
            String nextTwoFrame = frame.get(index + 2);
            if (nextTwoFrame.contains("X")){
                strikeScores = 30;
            }else {
                char[] nexttwoFrame = nextTwoFrame.toCharArray();
                if (nexttwoFrame[0] == '-'){
                    strikeScores = 20;
                }else {
                    strikeScores = 20 + Integer.parseInt(String.valueOf(nexttwoFrame[0]));
                }
            }
        }else {
            if (nextFrame.contains("/")){
                strikeScores = 20;
            }else {
                char[] nextframe = nextFrame.toCharArray();
                for (int i = 0;i < nextframe.length; i++){
                    if (nextframe[i] != '-'){
                        strikeScores = strikeScores + Integer.parseInt(String.valueOf(nextframe[i]));
                    }
                }
            }
        }
        return strikeScores;
    }

    //spare局的积分计算
    public static int spareScores(List<String> frame,int index){
        int spareScores = 10;
        String nextFrame = frame.get(index + 1);

        if (nextFrame.length() == 1){
            if (nextFrame.contains("X")){
                spareScores = spareScores + 10;
            }else if (nextFrame.contains("-")){
                spareScores = 10;
            }else {
                spareScores = spareScores + Integer.parseInt(nextFrame);
            }
        }else {
            char[] nextframe = nextFrame.toCharArray();
            if (nextframe[0] == '-'){
                spareScores = 10;
            }else {
                spareScores = spareScores + Integer.parseInt(String.valueOf(nextframe[0]));
            }
        }

        return spareScores;
    }

    //返回所有stirke局的索引，以便后续储存单局积分数组时，可对照进行strike局的存储
    public static List<Integer> allStrike(String scoreString){
        int count = 0;

        String notStrike = scoreString.replace("X","");
        int strikeNum = scoreString.length() - notStrike.length();
        List<Integer> indexes = new ArrayList<Integer>();

        for (int i = 0; i<strikeNum;i++){
            int index = scoreString.indexOf("X",count);
            indexes.add((index + i + 1)/2);
            count = index + 1;
        }

        return indexes;
    }
}

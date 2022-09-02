package com;

import java.io.*;
import java.util.*;

public class HuffmanCode {
    //将赫夫曼编码存放在Map<Byte,String>中，形如： a==>100
    static Map<Byte,String> huffmanCode = new HashMap<>();
    static StringBuilder stringBuilder = new StringBuilder();//用于拼接路径

    public static void main(String[] args) {
//        String content = "i like like like java do you like a java";//长度：40
//        //将字符串转成byte数组
//        byte[]  contentByte = content.getBytes();
        byte[] aaa={35,16,27,78,94,89,34,56,66,78,99,88,77,99,115,90,116,78};
        System.out.println("原始数据："+Arrays.toString(aaa));
        byte[] zip = huffmanZip(aaa);
        System.out.println("压缩的数据"+Arrays.toString(zip));
        System.out.println("码表：----------");
        for(Map.Entry<Byte,String> entry:huffmanCode.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }

        byte[] decode = decode(huffmanCode, zip);
        System.out.println("解压结果"+Arrays.toString(decode));
//        //压缩
//        byte[] zip = huffmanZip(contentByte);
//        System.out.println(Arrays.toString(zip));
//        //解压
//        byte[] decode = decode(huffmanCode, zip);
//        System.out.println(new String(decode));

        //对文件进行压缩
//        String fileSrc = "D:\\test.jpeg";
//        String desSrc = "D:\\test.zip";
//        fileZip(fileSrc,desSrc);
//        System.out.println("压缩成功");
        //对文件进行解压
       /* String fileSrc = "D:\\test.zip";
        String desSrc = "D:\\test2.jpeg";
        unZipFile(fileSrc,desSrc);
        System.out.println("解压成功");*/

    }

    /**
     * 对文件进行赫夫曼编码压缩
     * @param fileSrc 需要压缩文件的路径
     * @param desSrc  压缩后文件的输出路径
     */
    public static void fileZip(String fileSrc,String desSrc){
        //创建文件输入流
        InputStream input = null;
        //创建文件输出流,存放压缩文件
        OutputStream output = null;
        //创建对象输出流，以对象流的方式写入赫夫曼编码，为了以后恢复文件的时候使用
        ObjectOutput obput = null;
        try {
            input = new FileInputStream(fileSrc);//拿到文件的输入流
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            //对文件的字节数组进行压缩
            byte[] huffmanBytes = huffmanZip(bytes);
            output = new FileOutputStream(desSrc);
            obput = new ObjectOutputStream(output);
            obput.writeObject(huffmanBytes);//把赫夫曼编码后的字节数组写入压缩文件
            obput.writeObject(huffmanCode);//同时把赫夫曼编码写入压缩文件,以备以后解压
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                input.close();
                obput.close();
                output.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //解压缩文件
    public static void unZipFile(String fileSrc,String desSrc){
        //创建一个文件输入流对象
        InputStream input = null;
        //创建一个对象输入流对象
        ObjectInput OInput = null;
        //创建一个文件输出流
        OutputStream output = null;
        try {
            input = new FileInputStream(fileSrc);//读取目标文件
            OInput =  new ObjectInputStream(input);//实例化对象输入流
            byte[] huffmanByte = (byte[]) OInput.readObject();//获取文件中的赫夫曼字节数组
            Map<Byte,String> huffmanCode = (Map<Byte, String>) OInput.readObject();//读取文件中的赫夫曼编码表
            byte[] decode = decode(huffmanCode, huffmanByte);//解压
            output = new FileOutputStream(desSrc);//实例化输出流
            output.write(decode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                output.close();
                OInput.close();
                input.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     *
     * @param huffmanCode  每个字符对应的赫夫曼编码集合
     * @param huffmanByte  压缩后的byte数组
     * @return
     */
    private static byte[] decode(Map<Byte,String> huffmanCode,byte[] huffmanByte){
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<huffmanByte.length;i++){
            boolean flag = (i==huffmanByte.length-1);
            builder.append(byteToString(!flag,huffmanByte[i]));//就得到了所有byte数据对应的二进制字符串
        }
        Map<String,Byte> map = new HashMap<>();//存储每个二进制字符串对应的字符
        for(Map.Entry<Byte,String> entry:huffmanCode.entrySet()){
            map.put(entry.getValue(),entry.getKey());
        }
        List<Byte> list = new ArrayList<>();
        for(int i = 0;i<builder.length();){
            String key = "";
            int count = 1;
            boolean flag  = true;
            Byte b = null;
            while (flag){
                key = builder.substring(i,i+count);
                b = map.get(key);
                if(b == null){
                    //说明没有匹配到
                    count++;
                }else {
                    flag = false;
                }
            }
            list.add(b);
            i += count;//i移动到count
        }
        //此时list中存储的就是 每个字符的集合
        //将list集合转成数组
        byte[] bytes = new byte[list.size()];
        for(int i = 0;i<list.size();i++){
            bytes[i] = list.get(i);
        }
        return bytes;
    }



    /**
     *
     * @param flag 是否需要补高位，如果是最后一个字节无序补高位
     * @param b  需要转的byte数据
     * @return  b对应的二进制补码
     */
    public static String byteToString(boolean flag,byte b){
        int temp = b;//先将byte转成int
        if(flag){//如果是正数，需要补高位
            temp = temp | 256; //256==》1 0000 0000
        }
        String s = Integer.toBinaryString(temp);//将int类型数据转成二进制补码形式
        if(flag){
            return s.substring(s.length()-8);//截取最后八位
        }else {
            return s;
        }

    }

    //封装一下赫夫曼压缩数组的方法
    public static byte[] huffmanZip(byte[] contentByte){
        //将byte数组转成也给node集合
        List<Node> nodeList = getList(contentByte);
        //返回的是赫夫曼树的根节点
        Node root = huffmanTree(nodeList);
        //返回的是一个map集合，key是每个字符，value是字符对应的霍夫曼编码
        Map<Byte, String> code = getCode(root);
        //将霍夫曼编码集合压缩成一个byte数组
        byte[] zip = zip(contentByte, code);//长度：17
        return zip;
    }

    //获取一个node集合的方法
    public static List<Node> getList(byte[] bytes){
        ArrayList<Node> nodes = new ArrayList<>();
        //创建一个map，存放每个字母所对应的个数 key对应的是字符，value是次数
        HashMap<Byte, Integer> map = new HashMap<>();
        for(Byte b:bytes){
            Integer count = map.get(b);
            if(count==null){
                map.put(b,1);
            }else {
                map.put(b,count+1);
            }
        }
        //把每个键值对转成一个node对象，并加入到node集合
        for(Map.Entry<Byte,Integer> entry:map.entrySet()){
            nodes.add(new Node(entry.getKey(),entry.getValue()));
        }
        return nodes;
    }

    //通过集合创建一个赫夫曼树
    public static Node huffmanTree(List<Node> list){
        while (list.size()>1){
            Collections.sort(list);
            Node left = list.get(0);
            Node right = list.get(1);
            Node parent = new Node(left.weight + right.weight);
            parent.left = left;
            parent.right = right;
            list.remove(left);
            list.remove(right);
            list.add(parent);
        }
        return  list.get(0);
    }

    //重载getCode方法
    public static Map<Byte,String> getCode(Node root){
        if(root == null){
            return null;
        }
        getCode(root.left,"0",stringBuilder);
        getCode(root.right,"1",stringBuilder);
        return huffmanCode;
    }

    //将字符串转成的赫夫曼编码存到一个byte[]中
    private static byte[] zip(byte[] bytes,Map<Byte,String> huffmanCode){
        StringBuilder builder = new StringBuilder();
        for(byte b: bytes){
            //将每个字符对应的编码依次存到一个字符串中
            builder.append(huffmanCode.get(b));
        }
        int length;//初始化byte[]的长度
        int index = 0;//表示每个字符串数组的下标
        if(builder.length()%8 == 0){
            length  = builder.length() / 8;
        }else {
            length = builder.length() /8 +1;
        }
        byte[] huffmanCodeBytes = new byte[length];
        for(int i = 0;i<builder.length();i+=8){//每八位存到一个 byte数组中，这个八位是二进制补码
            String str;
            if(i+8>builder.length()){
                str = builder.substring(i);
            }else {
                str = builder.substring(i,i+8);
            }
            huffmanCodeBytes[index] = (byte) Integer.parseInt(str,2);//会将二进制补码转成原码，然后再转成十进制数
            index++;
        }
        return huffmanCodeBytes;
    }

    /**
     * 通过传入的node节点，获取其每个叶子节点的编码，存放到map集合中
     * @param node  节点
     * @param code  向左为0，向右为1
     * @param stringBuilder  拼接的字符串
     */
    public static void getCode(Node node,String code,StringBuilder stringBuilder){
        StringBuilder builder = new StringBuilder(stringBuilder);
        builder.append(code);
        if(node!=null){
            //判断节点是否为叶子节点
            if(node.data == null){
                //向左递归
                getCode(node.left,"0",builder);
                //向右递归
                getCode(node.right,"1",builder);
            }else {
                //说明当前节点是叶子节点
                huffmanCode.put(node.data,builder.toString());
            }
        }
    }
}

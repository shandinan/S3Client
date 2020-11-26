package com.star;

import java.io.*;

public class FileRead {

    public static void main(String[] args) throws IOException {
        File file = new File("F:\\Download\\HK_leftfront135257.mp4");
        FileInputStream fis = new FileInputStream(file);
        System.out.println("把本地文件转换成数据流");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len =0;
        byte[] bytes_temp = new byte[1024];
        while ((len=fis.read(bytes_temp)) != -1){
            bos.write(bytes_temp,0,len);
        }
        byte[] bytes = bos.toByteArray();
        fis.close();
        bos.close();
        System.out.println("数据转换完成");
    }
}

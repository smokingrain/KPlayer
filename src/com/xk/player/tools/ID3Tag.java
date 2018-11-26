package com.xk.player.tools;

import java.io.IOException;  
import java.io.InputStream;  
  
/** 
 * <b>MP3的ID3V2信息解析类</b> 
 *  
 * @QQ QQ:951868171 
 * @version 1.0 
 * @email xi_yf_001@126.com 
 * */  
public class ID3Tag {  
  
    private InputStream mp3ips;  
    public String charset = "GBK"; // 预设编码为GBK  
    private Id3v2Info info;  
  
    public ID3Tag(InputStream in) {  
        this.mp3ips = in;  
        info = new Id3v2Info("未知", "未知", "未知", null);  
    }  
  
    public void readId3v2() throws Exception {  
        try {  
            readId3v2(1024*100);        //读取前100KB  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
    /** 
     *  
     * */  
    public void readId3v2(int buffSize) throws Exception {  
        try {  
            if(buffSize > mp3ips.available()){  
                buffSize = mp3ips.available();  
            }  
            byte[] buff = new byte[buffSize];  
            mp3ips.read(buff, 0, buffSize);  
  
            if (ByteUtil.indexOf("ID3".getBytes(), buff, 1, 512) == -1)  
                throw new Exception("未发现ID3V2");  
            //获取头像  
            if (ByteUtil.indexOf("APIC".getBytes(), buff, 1, 512) != -1) {  
                int searLen = ByteUtil.indexOf(new byte[] { (byte) 0xFF,  
                        (byte) 0xFB }, buff);  
                int imgStart = ByteUtil.indexOf(new byte[] { (byte) 0xFF,  
                        (byte) 0xD8 }, buff);  
                int imgEnd = ByteUtil.lastIndexOf(new byte[] { (byte) 0xFF,  
                        (byte) 0xD9 }, buff, 1, searLen) + 2;  
                byte[] imgb = ByteUtil.cutBytes(imgStart, imgEnd, buff);  
                info.setApic(imgb);  
            }  
            if (ByteUtil.indexOf("TIT2".getBytes(), buff, 1, 512) != -1) {  
                info.setTit2(new String(readInfo(buff, "TIT2"), charset));  
                System.out.println("info:" + info.getTit2());  
            }  
            if (ByteUtil.indexOf("TPE1".getBytes(), buff, 1, 512) != -1) {  
                info.setTpe1(new String(readInfo(buff, "TPE1"), charset));  
                System.out.println("info:" + info.getTpe1());  
  
            }  
            if (ByteUtil.indexOf("TALB".getBytes(), buff, 1, 512) != -1) {  
                info.setTalb(new String(readInfo(buff, "TALB"), charset));  
                System.out.println("info:" + info.getTalb());  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
              
            mp3ips.close();  
        }  
  
    }  
  
    /** 
     *读取文本标签 
     **/  
    private byte[] readInfo(byte[] buff, String tag) {  
        int len = 0;  
        int offset = ByteUtil.indexOf(tag.getBytes(), buff);  
        len = buff[offset + 4] & 0xFF;  
        len = (len << 8) + (buff[offset + 5] & 0xFF);  
        len = (len << 8) + (buff[offset + 6] & 0xFF);  
        len = (len << 8) + (buff[offset + 7] & 0xFF);  
        len = len - 1;  
        return ByteUtil.cutBytes(ByteUtil.indexOf(tag.getBytes(), buff) + 11,  
                ByteUtil.indexOf(tag.getBytes(), buff) + 11 + len, buff);  
  
    }  
  
    public void setInfo(Id3v2Info info) {  
        this.info = info;  
    }  
  
    public Id3v2Info getInfo() {  
        return info;  
    }  
  
    public String getName() {  
        return getInfo().getTit2();  
  
    }  
  
    public String getAuthor() {  
  
        return getInfo().getTpe1();  
  
    }  
  
    public String getSpecial() {  
        return getInfo().getTalb();  
    }  
  
    public byte[] getImg() {  
        return getInfo().getApic();  
    }  
}  

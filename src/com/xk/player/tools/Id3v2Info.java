package com.xk.player.tools;

public class Id3v2Info {  
    // 歌名  
    private String tit2 = null;  
    // 艺术家  
    private String tpe1 = null;  
    // 专辑  
    private String talb = null;  
    // 头像  
    private byte[] apic = null;  
  
    public Id3v2Info(String tit2, String tpe1, String talb, byte[] apic) {  
        setTit2(tit2);  
        setTpe1(tpe1);  
        setTalb(talb);  
        setApic(apic);  
  
    }  
  
    public void setTit2(String tit2) {  
        this.tit2 = tit2;  
    }  
  
    public String getTit2() {  
        return tit2;  
    }  
  
    public void setTpe1(String tpe1) {  
        this.tpe1 = tpe1;  
    }  
  
    public String getTpe1() {  
        return tpe1;  
    }  
  
    public void setTalb(String talb) {  
        this.talb = talb;  
    }  
  
    public String getTalb() {  
        return talb;  
    }  
  
    public void setApic(byte[] apic) {  
        this.apic = apic;  
    }  
  
    public byte[] getApic() {  
        return apic;  
    }  
  
}  
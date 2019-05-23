/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xk.player.tools;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;


/**
 * 一个工具类，主要负责分析歌词
 * 并找到歌词下载下来，然后保存成标准格式的文件
 * 还有一些常用的方法
 * @author hadeslee
 */
public final class Util {

    public static String VERSION = "1.2";//版本号,用于对比更新
    private static final JPanel panel = new JPanel();

    private Util() {
    }


    public static boolean changeLocalSourceToMp3(String localFilePath, String targetPath) {

		File source = new File(localFilePath);
		File target = new File(targetPath);
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();

		audio.setCodec("libmp3lame");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);
		try {
			encoder.encode(source, target, attrs);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}


    /**
     * 一个简便的生成一系列渐变颜色的方法,一般是生成128
     * 个颜个,供可视化窗口用
     * @param c1 第一种颜色
     * @param c2 第二种颜色
     * @param c3 第三种颜色
     * @param count 生成几种颜色
     * @return 渐变色
     */
    public static Color[] getColors(Color c1, Color c2, Color c3, int count) {
        if (count < 3) {
            throw new IllegalArgumentException("总颜色数不能少于3!");
        }
        Color[] cs = new Color[count];
        int half = count / 2;
        float addR = (c2.getRed() - c1.getRed()) * 1.0f / half;
        float addG = (c2.getGreen() - c1.getGreen()) * 1.0f / half;
        float addB = (c2.getBlue() - c1.getBlue()) * 1.0f / half;
//        log.log(Level.INFO, "addR="+addR+",addG="+addG+",addB="+addB);
        int r = c1.getRed();
        int g = c1.getGreen();
        int b = c1.getBlue();
        for (int i = 0; i < half; i++) {
            cs[i] = new Color((int) (r + i * addR), (int) (g + i * addG), (int) (b + i * addB));
//            log.log(Level.INFO, "cs["+i+"]="+cs[i]);
        }
        addR = (c3.getRed() - c2.getRed()) * 1.0f / half;
        addG = (c3.getGreen() - c2.getGreen()) * 1.0f / half;
        addB = (c3.getBlue() - c2.getBlue()) * 1.0f / half;
        r = c2.getRed();
        g = c2.getGreen();
        b = c2.getBlue();
        for (int i = half; i < count; i++) {
            cs[i] = new Color((int) (r + (i - half) * addR), (int) (g + (i - half) * addG), (int) (b + (i - half) * addB));
//            log.log(Level.INFO, "cs["+i+"]="+cs[i]);
        }
        return cs;
    }

    /**
     * 根据特定的颜色生成一个图标的方法
     * @param c 颜色
     * @param width 宽度
     * @param height 高度
     * @return 图标
     */
    public static ImageIcon createColorIcon(Color c, int width, int height) {
        BufferedImage bi = createImage(c, width, height);
        return new ImageIcon(bi);
    }

    /**
     * 根据特定的颜色,生成这个颜色的一张图片
     * 一般用于显示在图片按钮上做为ICON的
     * @param c 颜色
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return 生成的图片
     */
    public static BufferedImage createImage(Color c, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(c);
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(128, 128, 128));
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(new Color(236, 233, 216));
        g.drawRect(1, 1, width - 3, height - 3);
        return bi;
    }




	/**
     * 从一个int值得到它所代表的字节数组
     * @param i 值 
     * @return 字节数组
     */
    public static byte[] getBytesFromInt(int i) {
        byte[] data = new byte[4];
        data[0] = (byte) (i & 0xff);
        data[1] = (byte) ((i >> 8) & 0xff);
        data[2] = (byte) ((i >> 16) & 0xff);
        data[3] = (byte) ((i >> 24) & 0xff);
        return data;
    }


    /**
     * 一个简便的方法，把一个字符串的转成另一种字符串
     * @param source 源字符串
     * @param encoding 编码
     * @return 新的字符串
     */
    public static String convertString(String source, String encoding) {
        try {
            byte[] data = source.getBytes("ISO8859-1");
            return new String(data, encoding);
        } catch (UnsupportedEncodingException ex) {
            return source;
        }
    }

    /**
     * 转码的一个方便的方法
     * @param source 要转的字符串
     * @param sourceEnc 字符串原来的编码
     * @param distEnc 要转成的编码
     * @return 转后的字符串
     */
    public static String convertString(String source, String sourceEnc, String distEnc) {
        try {
            byte[] data = source.getBytes(sourceEnc);
            return new String(data, distEnc);
        } catch (UnsupportedEncodingException ex) {
            return source;
        }
    }

    /**
     * 从传进来的数得到这个数组
     * 组成的整型的大小
     * @param data 数组
     * @return 整型
     */
    public static int getInt(byte[] data) {
        if (data.length != 4) {
            throw new IllegalArgumentException("数组长度非法,要长度为4!");
        }
        return (data[0] & 0xff) | ((data[1] & 0xff) << 8) | ((data[2] & 0xff) << 16) | ((data[3] & 0xff) << 24);
    }

    /**
     * 从传进来的字节数组得到
     * 这个字节数组能组成的长整型的结果
     * @param data 字节数组
     * @return 长整型
     */
    public static long getLong(byte[] data) {
        if (data.length != 8) {
            throw new IllegalArgumentException("数组长度非法,要长度为4!");
        }
        return (data[0] & 0xff) |
                ((data[1] & 0xff) << 8) |
                ((data[2] & 0xff) << 16) |
                ((data[3] & 0xff) << 24) |
                ((data[4] & 0xff) << 32) |
                ((data[5] & 0xff) << 40) |
                ((data[6] & 0xff) << 48) |
                ((data[7] & 0xff) << 56);
    }


    /**
     * 根据一个文件的全路径得到它的扩展名
     * @param path 全路径
     * @return  扩展名
     */
    public static String getExtName(String path) {
        return path.substring(path.lastIndexOf(".") + 1);
    }

    /**
     * 得到两个矩形的距离
     * @param rec1 矩形1
     * @param rec2 矩形2
     * @return 距离
     */
    public static int getDistance(Rectangle rec1, Rectangle rec2) {
        if (rec1.intersects(rec2)) {
            return Integer.MAX_VALUE;
        }
        int x1 = (int) rec1.getCenterX();
        int y1 = (int) rec1.getCenterY();
        int x2 = (int) rec2.getCenterX();
        int y2 = (int) rec2.getCenterY();
        int dis1 = Math.abs(x1 - x2) - rec1.width / 2 - rec2.width / 2;
        int dis2 = Math.abs(y1 - y2) - rec1.height / 2 - rec2.height / 2;
        return Math.max(dis1, dis2) - 1;
    }


    /**
     * 根据一些参数快速地构造出按钮来
     * 这些按钮从外观上看都是一些特殊的按钮
     * @param name 按钮图片的相对地址
     * @param cmd 命令
     * @param listener 监听器
     * @return 按钮
     */
    public static JButton createJButton(String name, String cmd, ActionListener listener) {
        Image[] icons = Util.getImages(name, 3);
        JButton jb = new JButton();
        jb.setBorderPainted(false);
        jb.setFocusPainted(false);
        jb.setContentAreaFilled(false);
        jb.setDoubleBuffered(true);
        jb.setIcon(new ImageIcon(icons[0]));
        jb.setRolloverIcon(new ImageIcon(icons[1]));
        jb.setPressedIcon(new ImageIcon(icons[2]));
        jb.setOpaque(false);
        jb.setFocusable(false);
        jb.setActionCommand(cmd);
        jb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jb.addActionListener(listener);
        return jb;
    }

    /**
     * 根据一些参数快速地构造出按钮来
     * 这些按钮从外观上看都是一些特殊的按钮
     * @param name 按钮图片的相对地址
     * @param cmd 命令
     * @param listener 监听器
     * @param selected 是否被选中了
     * @return 按钮
     */
    public static JToggleButton createJToggleButton(String name, String cmd, ActionListener listener, boolean selected) {
        Image[] icons = Util.getImages(name, 3);
        JToggleButton jt = new JToggleButton();
        jt.setBorder(null);
        jt.setContentAreaFilled(false);
        jt.setFocusPainted(false);
        jt.setDoubleBuffered(true);
        jt.setIcon(new ImageIcon(icons[0]));
        jt.setRolloverIcon(new ImageIcon(icons[1]));
        jt.setSelectedIcon(new ImageIcon(icons[2]));
        jt.setOpaque(false);
        jt.setFocusable(false);
        jt.setActionCommand(cmd);
        jt.setSelected(selected);
        jt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jt.addActionListener(listener);
        return jt;
    }

    /**
     * 得到一系列的图片，以数字递增做为序列的
     * @param who 图片的基名
     * @param count 数量
     * @return 图片数组
     */
    public static Image[] getImages(String who, int count) {
        Image[] imgs = new Image[3];
        MediaTracker mt = new MediaTracker(panel);
        Toolkit tk = Toolkit.getDefaultToolkit();
        for (int i = 1; i <= count; i++) {
            URL url = Util.class.getResource("/com/hadeslee/yoyoplayer/pic/" + who + i + ".png");
            imgs[i - 1] = tk.createImage(url);
            mt.addImage(imgs[i - 1], i);
        }
        try {
            mt.waitForAll();
        } catch (Exception exe) {
            exe.printStackTrace();
        }

        return imgs;
    }

    /**
     * 根据某个URL得到这个URL代表的图片
     * 并且把该图片导入内存
     * @param name URL
     * @return 图片
     */
    public static Image getImage(String name) {
        URL url = Util.class.getResource("/com/hadeslee/yoyoplayer/pic/" + name);
        Image im = Toolkit.getDefaultToolkit().createImage(url);
        try {
            MediaTracker mt = new MediaTracker(panel);
            mt.addImage(im, 0);
            mt.waitForAll();
        } catch (Exception exe) {
            exe.printStackTrace();
        }
        return im;
    }

    /**
     * 根据一个比例得到两种颜色之间的渐变色
     * @param c1 第一种颜色
     * @param c2 第二种颜色
     * @param f 比例
     * @return 新的颜色
     */
    public static Color getGradientColor(Color c1, Color c2, float f) {
        int deltaR = c2.getRed() - c1.getRed();
        int deltaG = c2.getGreen() - c1.getGreen();
        int deltaB = c2.getBlue() - c1.getBlue();
        int r1 = (int) (c1.getRed() + f * deltaR);
        int g1 = (int) (c1.getGreen() + f * deltaG);
        int b1 = (int) (c1.getBlue() + f * deltaB);
        Color c = new Color(r1, g1, b1);
        return c;
    }

    /**
     * 得到两种颜色的混合色
     * @param c1 第一种颜色
     * @param c2 第二种颜色
     * @return 混合色
     */
    public static Color getColor(Color c1, Color c2) {
        int r = (c2.getRed() + c1.getRed()) / 2;
        int g = (c2.getGreen() + c1.getGreen()) / 2;
        int b = (c2.getBlue() + c1.getBlue()) / 2;
        return new Color(r, g, b);
    }

    /**
     * 一个简便地获取字符串高度的方法
     * @param s 字符串
     * @param g 画笔
     * @return 高度
     */
    public static int getStringHeight(String s, Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
    }

    /**
     * 一个简便地获取字符串宽度的方法
     * @param s 字符串
     * @param g 画笔
     * @return 宽度
     */
    public static int getStringWidth(String s, Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
    }

    /**
     * 自定义的画字符串的方法，从字符串的左上角开始画
     * 不是JAVA的从左下角开始的画法
     * @param g 画笔
     * @param s 字符串
     * @param x X坐标
     * @param y Y坐标
     */
    public static void drawString(Graphics g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        g.drawString(s, x, y + asc);
    }

    /**
     * 一个简便的让字符串对于某点居中的画法
     * @param g 画笔
     * @param s 字符串
     * @param x X坐标
     * @param y Y坐标
     */
    public static void drawStringCenter(Graphics g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int width = getStringWidth(s, g);
        g.drawString(s, x - width / 2, y + asc);
    }

    /**
     * 一个便捷的方法,画字符串右对齐的方法
     * @param g 画笔
     * @param s 字符串
     * @param x 右对齐的X座标
     * @param y 右对齐的Y座标
     */
    public static void drawStringRight(Graphics g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int width = getStringWidth(s, g);
        g.drawString(s, x - width, y + asc);
    }

    /**
     * 得到文件的格式
     * @param f 文件
     * @return 格式
     */
    public static String getType(File f) {
        String name = f.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * 根据文件名得到歌曲的名字
     * @param f 文件名
     * @return 歌曲名
     */
    public static String getSongName(File f) {
        String name = f.getName();
        name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

    /**
     * 根据文件名得到歌曲的名字
     * @param name 文件名
     * @return 歌曲名
     */
    public static String getSongName(String name) {
        try {
            int index = name.lastIndexOf(File.separator);
            name = name.substring(index + 1, name.lastIndexOf("."));
            return name;
        } catch (Exception exe) {
            return name;
        }

    }



    /**
     * 秒数转成00:00之类的字符串
     * @param sec 秒数
     * @return 字符串
     */
    public static String secondToString(int sec) {
        DecimalFormat df = new DecimalFormat("00");
        StringBuilder sb = new StringBuilder();
        sb.append(df.format(sec / 60)).append(":").append(df.format(sec % 60));
        return sb.toString();
    }



    /**
     * 去除HTML标记
     * @param str1 含有HTML标记的字符串
     * @return 去除掉相关字符串
     */
    public static String htmlTrim(String str1) {
        String str = "";
        str = str1;
        //剔出了<html>的标签
        str = str.replaceAll("</?[^>]+>", "");
        //去除空格
        str = str.replaceAll("\\s", "");
        str = str.replaceAll("&nbsp;", "");
        str = str.replaceAll("&amp;", "&");
        str = str.replace(".", "");
        str = str.replace("\"", "‘");
        str = str.replace("'", "‘");
        return str;
    }

    public static String htmlToString(String string) {
        String ans = string.replaceAll("&quot;", "\"");
        ans = ans.replaceAll("&amp;", "&");
        ans = ans.replaceAll("&lt;", "<");
        ans = ans.replaceAll("&gt;", ">");
        ans = ans.replaceAll("<.+?>", "");

        return ans;
    }

    public static String formatSeconds(double seconds, int precision) {
        int min = (int) ((Math.round(seconds)) / 60);
        int hrs = min / 60;
        if (min > 0) seconds -= min * 60;
        if (seconds < 0) seconds = 0;
        if (hrs > 0) min -= hrs * 60;
        int days = hrs / 24;
        if (days > 0) hrs -= days * 24;
        int weeks = days / 7;
        if (weeks > 0) days -= weeks * 7;

        StringBuilder builder = new StringBuilder();
        if (weeks > 0) builder.append(weeks).append("wk ");
        if (days > 0) builder.append(days).append("d ");
        if (hrs > 0) builder.append(hrs).append(":");
        if (hrs > 0 && min < 10) builder.append("0");
        builder.append(min).append(":");
//        int n = precision + ((precision == 0) ? 2 : 3);
//        String fmt = "%0" + n + "." + precision + "f";
//        builder.append(new Formatter().format(Locale.US, fmt, seconds));
        int sec = (int) seconds;
        if (sec < 10) builder.append("0");
        builder.append(Math.round(sec));
        return builder.toString();
    }


    public static String getFileExt(File file) {
        return getFileExt(file.getName());
    }

    public static String removeExt(String s) {
        int index = s.lastIndexOf(".");
        if (index == -1) index = s.length();
        return s.substring(0, index);
    }

    public static String getFileExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos == -1) return "";
        return fileName.substring(pos + 1).toLowerCase();
    }

    public static String longest(String... args) {
        if (args.length == 0) return "";
        String longest = args[0] == null ? "" : args[0];
        for (String s : args)
            if (s != null && s.length() > longest.length())
                longest = s;
        return longest;
    }

    public static String firstNotEmpty(String... values) {
        for (String value : values) {
            if (value != null && !value.isEmpty())
                return value;
        }

        return null;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isEmpty(List values) {
        return values == null || values.isEmpty();
    }

    public static boolean isEmpty(Set values) {
        return values == null || values.isEmpty();
    }

    public static String humanize(String property) {
        String s = property.replaceAll("(?=\\p{Upper})", " ");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String capitalize(String str, String delim) {
        str = str.replaceAll("&", "and");
        String[] strings = str.split("[ _]+");
        final StringBuilder sb = new StringBuilder();

        for (String s : strings) {
            s = s.toLowerCase();
            sb.append(s.substring(0, 1).toUpperCase());
            sb.append(s.substring(1)).append(delim);
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static Color getContrastColor(Color bg) {
        int threshold = 105;
        int delta = (int) (bg.getRed() * 0.299 + bg.getGreen() * 0.587 + bg.getBlue() * 0.114);
        return (255 - delta < threshold) ? Color.black : Color.white;
    }

    public static void fixIconTextGap(JComponent menu) {
        if (isNimbusLaF()) {
            Component[] components = menu.getComponents();
            for (Component component : components) {
                if (component instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) component;
                    b.setIconTextGap(0);
                }

                if (component instanceof JMenu)
                    fixIconTextGap(((JMenu) component).getPopupMenu());
            }
        }
    }

    public static boolean isNimbusLaF() {
        return UIManager.getLookAndFeel().getName().contains("Nimbus");
    }

    public static boolean isWindowsLaF() {
        return UIManager.getLookAndFeel().getName().contains("Windows");
    }

    public static boolean isGTKLaF() {
        return UIManager.getLookAndFeel().getName().contains("GTK");
    }

    public static String center(String str, int maxSize, int size) {
        if (str == null || size <= 0 || str.length() >= maxSize) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2);
        str = rightPad(str, size);
        return str;
    }

    public static String rightPad(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String leftPad(String s, int n) {
        return String.format("%1$#" + n + "s", s);
    }

    public static String formatFieldValues(Object values) {
		String result = null;

		if (values != null) {
        	if (values instanceof String) {
        		result = (String) values;
        	}
        }
        
        return result;
    }
    
    public static String join(Collection<? extends Object> collection, String toJoin) {
    	StringBuffer sb = new StringBuffer();
    	int index = 0;
    	for(Object element : collection) {
    		if(null == element) {
    			continue;
    		}
    		sb.append(element);
    		if(++index == collection.size()) {
    			break;
    		}
    		sb.append(toJoin);
    	}
    	return sb.toString();
    }

    public static String formatFieldValues(Object values, String separator) {
        if (values != null && separator != null) {
        	if (values instanceof String) {
        		return (String) values;
        	}
        	else if (values instanceof Object[]) {
            	StringBuilder sb = new StringBuilder();
            	Object[] vs = (Object[]) values;
            	for (int i = 0; i < vs.length; i++) {
            		String value = vs[i].toString();
            		if (sb.length() != 0) {
            			sb.append(separator);
            		}
            		sb.append(value == null ? "" : value.toString());
            	}
            	return sb.toString();
        	}
        	else if (values instanceof List) {
            	StringBuilder sb = new StringBuilder();
            	List<Object> vs = (List<Object>) values;
            	for (Object obj : vs) {
            		String value = obj.toString();
            		if (sb.length() != 0) {
            			sb.append(separator);
            		}
            		sb.append(value == null ? "" : value.toString());
            	}
            	return sb.toString();
        	}
        }
        
        return null;
    }

}


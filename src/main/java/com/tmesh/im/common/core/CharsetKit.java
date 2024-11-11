package com.tmesh.im.common.core;


import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 字符集工具类
 */
public class CharsetKit {
    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * UTF-8
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * GBK
     */
    public static final String GBK = "GBK";

    /**
     * ISO-8859-1
     */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1);
    /**
     * UTF-8
     */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);
    /**
     * GBK
     */
    public static final Charset CHARSET_GBK = Charset.forName(GBK);

    /**
     * 转换为 Charset 对象
     * <p>
     * charset 字符集，为空则返回默认字符集
     */
    public static Charset charset(String charse) {
        return StringUtils.hasText(charse) ? Charset.forName(charse) : Charset.defaultCharset();
    }

    /**
     * 转换字符串的字符集编码
     * <p>
     * source      字符串
     * srcCharset  源字符集，默认 ISO-8859-1
     * destCharset 目标字符集，默认 UTF-8
     * 转换后的字符集
     */
    public static String convert(String source, String srcCharset, String destCharset) {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
    }

    /**
     * 转换字符串的字符集编码
     * <p>
     * source      字符串
     * srcCharset  源字符集，默认ISO-8859-1
     * destCharset 目标字符集，默认UTF-8
     * 转换后的字符集
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset) {
        if (srcCharset == null) {
            srcCharset = StandardCharsets.ISO_8859_1;
        }
        
        if (destCharset == null) {
            destCharset = StandardCharsets.UTF_8;
        }
        
        if (!StringUtils.hasText(source) || srcCharset.equals(destCharset)) {
            return source;
        }
        
        return new String(source.getBytes(srcCharset), destCharset);
    }

    /**
     * 系统字符集编码
     */
    public static String sysCharset() {
        return Charset.defaultCharset().name();
    }
}

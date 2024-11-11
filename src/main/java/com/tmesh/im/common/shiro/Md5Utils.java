package com.tmesh.im.common.shiro;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : Md5Util
 */
public class Md5Utils {

    /**
     * 基础密码
     */
    private static final String BASE_STR = "abcdefghgkmnprstwxyz123456789";

    /**
     * credentials
     * @param password
     * @param salt 盐：为了即使相同的密码不同的盐加密后的结果也不同
     */
    public static final String credentials(String password, String salt) {
        ByteSource byteSalt = ByteSource.Util.bytes(salt);
        Md5Hash hash = new Md5Hash(password, byteSalt);
        return hash.toString();
    }

    /**
     * md5
     */
    public static final String md5(String str) {
        return SecureUtil.md5(str);
    }

    /**
     * 加密盐
     */
    public static String salt() {
        return RandomUtil.randomString(4);
    }

    /**
     * 随机密码
     */
    public static String password() {
        return RandomUtil.randomString(Md5Utils.BASE_STR, 8);
    }
}

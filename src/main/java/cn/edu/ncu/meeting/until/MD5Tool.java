package cn.edu.ncu.meeting.until;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {
    private static MessageDigest md5;
    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(String string) {
        md5.update(StandardCharsets.UTF_8.encode(string));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }
}

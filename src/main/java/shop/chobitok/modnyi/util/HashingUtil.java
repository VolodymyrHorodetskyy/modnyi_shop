package shop.chobitok.modnyi.util;

import org.apache.commons.codec.digest.DigestUtils;

public class HashingUtil {

    public static String hashSha256(String toHash) {
        return DigestUtils.sha256Hex(toHash);
    }

}

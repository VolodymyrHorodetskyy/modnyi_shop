package shop.chobitok.modnyi.util;

import org.apache.commons.codec.digest.DigestUtils;

import static org.springframework.util.StringUtils.isEmpty;

public class HashingUtil {

    public static String hashSha256(String toHash) {
        if (!isEmpty(toHash)) {
            return DigestUtils.sha256Hex(toHash);
        }
        return toHash;
    }

}

package org.labs.lab6_auth.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;

public class TotpUtil {
    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160-bit secret
        random.nextBytes(bytes);
        return base32Encode(bytes);
    }

    private static String base32Encode(byte[] data) {
        StringBuilder result = new StringBuilder();
        int index = 0, digit = 0;
        int currByte, nextByte;
        for (int i = 0; i < data.length; ) {
            currByte = (data[i] >= 0) ? data[i] : (data[i] + 256);

            if (index > 3) {
                if ((i + 1) < data.length) {
                    nextByte = (data[i + 1] >= 0) ? data[i + 1] : (data[i + 1] + 256);
                } else {
                    nextByte = 0;
                }

                digit = currByte & (0xFF >> index);
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> (8 - index);
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 0x1F;
                index = (index + 5) % 8;
                if (index == 0) i++;
            }
            result.append(BASE32_ALPHABET.charAt(digit));
        }
        // no padding
        return result.toString();
    }

    // Create otpauth URL for QR code (Google Authenticator)
    public static String getOtpAuthURL(String issuer, String accountName, String secret) {
        try {
            String label = URLEncoder.encode(issuer + ":" + accountName, StandardCharsets.UTF_8);
            String issuerEnc = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
            return String.format("otpauth://totp/%s?secret=%s&issuer=%s&period=30", label, secret, issuerEnc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Verify TOTP code (6 digits). Allow window = 1 (30s steps backward/forward)
    public static boolean verifyCode(String secret, int code, int window) {
        long timeIndex = Instant.now().getEpochSecond() / 30;
        for (int i = -window; i <= window; ++i) {
            long idx = timeIndex + i;
            int generated = generateTOTP(secret, idx);
            if (generated == code) return true;
        }
        return false;
    }

    private static int generateTOTP(String base32Secret, long timeIndex) {
        byte[] key = base32Decode(base32Secret);
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(timeIndex);
            byte[] data = buffer.array();

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            int binary =
                    ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

            int otp = binary % 1000000;
            return otp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] base32Decode(String base32) {
        base32 = base32.replace("=", "").toUpperCase();
        int numBytes = base32.length() * 5 / 8;
        byte[] result = new byte[numBytes];

        int buffer = 0, bitsLeft = 0, count = 0;
        for (char c : base32.toCharArray()) {
            int val = BASE32_ALPHABET.indexOf(c);
            if (val < 0) continue;
            buffer <<= 5;
            buffer |= val & 31;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[count++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        return result;
    }
}

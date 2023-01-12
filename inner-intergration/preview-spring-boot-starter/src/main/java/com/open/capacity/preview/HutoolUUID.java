package com.open.capacity.preview;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * <p>
 * <p/>
 *
 * @author luowj
 * @version 1.0
 * @since 2022/7/13 18:33
 * https://gitee.com/kekingcn/file-online-preview
 * https://github.com/TomHusky/kkfilemini-spring-boot-starter
 */
public class HutoolUUID implements Serializable, Comparable<HutoolUUID> {

    private static final long serialVersionUID = -1185015143654744140L;
    private final long mostSigBits;
    private final long leastSigBits;

    private HutoolUUID(byte[] data) {
        long msb = 0L;
        long lsb = 0L;

        assert data.length == 16 : "data must be 16 bytes in length";

        int i;
        for (i = 0; i < 8; ++i) {
            msb = msb << 8 | (data[i] & 255);
        }

        for (i = 8; i < 16; ++i) {
            lsb = lsb << 8 | (data[i] & 255);
        }

        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    public HutoolUUID(long mostSigBits, long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    public static HutoolUUID fastUUID() {
        return randomUUID(false);
    }

    public static HutoolUUID randomUUID() {
        return randomUUID(true);
    }

    public static HutoolUUID randomUUID(boolean isSecure) {
        Random ng = isSecure ? HutoolUUID.Holder.NUMBER_GENERATOR : ThreadLocalRandom.current();
        byte[] randomBytes = new byte[16];
        (ng).nextBytes(randomBytes);
        randomBytes[6] = (byte) (randomBytes[6] & 15);
        randomBytes[6] = (byte) (randomBytes[6] | 64);
        randomBytes[8] = (byte) (randomBytes[8] & 63);
        randomBytes[8] = (byte) (randomBytes[8] | 128);
        return new HutoolUUID(randomBytes);
    }

    public static HutoolUUID nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new InternalError("MD5 not supported");
        }

        byte[] md5Bytes = md.digest(name);
        md5Bytes[6] = (byte) (md5Bytes[6] & 15);
        md5Bytes[6] = (byte) (md5Bytes[6] | 48);
        md5Bytes[8] = (byte) (md5Bytes[8] & 63);
        md5Bytes[8] = (byte) (md5Bytes[8] | 128);
        return new HutoolUUID(md5Bytes);
    }

    public static HutoolUUID fromString(String name) {
        String[] components = name.split("-");
        if (components.length != 5) {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        } else {
            for (int i = 0; i < 5; ++i) {
                components[i] = "0x" + components[i];
            }

            long mostSigBits = Long.decode(components[0]);
            mostSigBits <<= 16;
            mostSigBits |= Long.decode(components[1]);
            mostSigBits <<= 16;
            mostSigBits |= Long.decode(components[2]);
            long leastSigBits = Long.decode(components[3]);
            leastSigBits <<= 48;
            leastSigBits |= Long.decode(components[4]);
            return new HutoolUUID(mostSigBits, leastSigBits);
        }
    }

    public long getLeastSignificantBits() {
        return this.leastSigBits;
    }

    public long getMostSignificantBits() {
        return this.mostSigBits;
    }

    public int version() {
        return (int) (this.mostSigBits >> 12 & 15L);
    }

    public int variant() {
        return (int) (this.leastSigBits >>> (int) (64L - (this.leastSigBits >>> 62)) & this.leastSigBits >> 63);
    }

    public long timestamp() throws UnsupportedOperationException {
        this.checkTimeBase();
        return (this.mostSigBits & 4095L) << 48 | (this.mostSigBits >> 16 & 65535L) << 32 | this.mostSigBits >>> 32;
    }

    public int clockSequence() throws UnsupportedOperationException {
        this.checkTimeBase();
        return (int) ((this.leastSigBits & 4611404543450677248L) >>> 48);
    }

    public long node() throws UnsupportedOperationException {
        this.checkTimeBase();
        return this.leastSigBits & 281474976710655L;
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean isSimple) {
        StringBuilder builder = new StringBuilder(isSimple ? 32 : 36);
        builder.append(digits(this.mostSigBits >> 32, 8));
        if (!isSimple) {
            builder.append('-');
        }

        builder.append(digits(this.mostSigBits >> 16, 4));
        if (!isSimple) {
            builder.append('-');
        }

        builder.append(digits(this.mostSigBits, 4));
        if (!isSimple) {
            builder.append('-');
        }

        builder.append(digits(this.leastSigBits >> 48, 4));
        if (!isSimple) {
            builder.append('-');
        }

        builder.append(digits(this.leastSigBits, 12));
        return builder.toString();
    }

    public int hashCode() {
        long hilo = this.mostSigBits ^ this.leastSigBits;
        return (int) (hilo >> 32) ^ (int) hilo;
    }

    public boolean equals(Object obj) {
        if (null != obj && obj.getClass() == UUID.class) {
            HutoolUUID id = (HutoolUUID) obj;
            return this.mostSigBits == id.mostSigBits && this.leastSigBits == id.leastSigBits;
        } else {
            return false;
        }
    }

    public int compareTo(HutoolUUID val) {
        int compare = Long.compare(this.mostSigBits, val.mostSigBits);
        if (0 == compare) {
            compare = Long.compare(this.leastSigBits, val.leastSigBits);
        }

        return compare;
    }

    private static String digits(long val, int digits) {
        long hi = 1L << digits * 4;
        return Long.toHexString(hi | val & hi - 1L).substring(1);
    }

    private void checkTimeBase() {
        if (this.version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
    }

    private static class Holder {
        static final SecureRandom NUMBER_GENERATOR = new SecureRandom();

        private Holder() {
        }
    }
}

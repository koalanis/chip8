package io.chip8.bitlogic;

/**
 * Created by kaleb on 1/5/2017.
 */
public class BinaryLogic {

    // Solution found on stackoverflow. Probably could have written myself but too lazy
    // http://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    public static String shortToHexString(short s) {
        return Integer.toHexString( s & 0xffff);
    }
}

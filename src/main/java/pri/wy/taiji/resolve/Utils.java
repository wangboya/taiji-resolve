package pri.wy.taiji.resolve;

public class Utils {

    public static String format(long val, int len) {
        char[] chs = new char[len];
        for (int i = 0; i < len; i++) {
            chs[i] = (val & (0x1 << (len - 1 - i))) > 0 ? '1' : '0';
        }
        return new String(chs);
    }


    public static long valOf(int... bts) {
        long val = 0;
        for (int i = 0; i < bts.length; i++) {
            val = (val << 1) + (bts[i] & 0x1);
        }
        return val;
    }

    public static long valOf(String val) {
        int[] bts = new int[val.length()];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = '1' == val.charAt(i) ? 1 : 0;
        }
        return valOf(bts);
    }
}

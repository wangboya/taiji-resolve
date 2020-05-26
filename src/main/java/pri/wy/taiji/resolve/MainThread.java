package pri.wy.taiji.resolve;

import static pri.wy.taiji.resolve.Utils.valOf;

public class MainThread {
    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        String start = args[0];
        String mask = args[1];
        if (start.length() != mask.length()) {
            System.out.println("length mismatch!");
        }
        int len = start.length();
        char[] ch = new char[len];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = '1';
        }
        String tar = new String(ch);
        Resolver all1 = new Resolver(
                len,
                valOf(start),
                valOf(tar),
                valOf(mask)
        );
        for (int i = 0; i < ch.length; i++) {
            ch[i] = '0';
        }
        tar = new String(ch);
        Resolver all0 = new Resolver(
                len,
                valOf(start),
                valOf(tar),
                valOf(mask)
        );
        new Thread(() -> {

            long[] rst = all1.resolve();
            all0.stop();
            if (rst != null) {
                System.out.println(all1);
                System.out.println("result:");
                System.out.println(all1.resolve(rst));
                System.out.println("total cost: " + (System.currentTimeMillis() - t) + "ms");
            }

        }).start();

        new Thread(() -> {

            long[] rst = all0.resolve();
            all1.stop();
            if (rst != null) {
                System.out.println(all0);
                System.out.println("result:");
                System.out.println(all0.resolve(rst));
                System.out.println("total cost: " + (System.currentTimeMillis() - t) + "ms");
            }

        }).start();
    }
}

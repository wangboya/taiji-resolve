package pri.wy.taiji.resolve;

import static pri.wy.taiji.resolve.Utils.valOf;

public class MainThread {
     /**
     * 第一个参数是当前的布局,1代表黑色,0代表白色,顺时针例如
     * 10001000,表示总共有8个,其中第一个,第五个是黑色
     * 第二个参数是当前的线条布局,为了方便,从第一个线条开始,1代表有线条,0代表无线条,顺时针例如
     * 11010000,表示第1,2,4的位置有线条
     * 如果结果求出来了,
     * 那么只要看'@'符号的数值,表示把第一个线条放在第几个位置
     * @param args
     */
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

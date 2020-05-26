package pri.wy.taiji.resolve;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static pri.wy.taiji.resolve.Utils.format;

public class Resolver {

    private final int length;

    public final long source;

    public final long target;
    public final long mask;

    public final long[] masks;

    private Map<Long, Long> sourceMap = new HashMap<>();

    private Map<Long, Long> targetMap = new HashMap<>();
    private boolean run = true;

    public Resolver(int length, long source, long target, long step) {
        long mask = 1;
        this.length = length;
        int i = length;
        while (--i > 0) {
            mask = (mask << 1) + 1;//计算掩码
        }
        this.mask = mask;
        masks = new long[length];
        masks[i] = step;
        for (i = 1; i < length; i++) {
            long first = step & 0x1;
            step = step >>> 1;
            step = step | (first << (length - 1));
            masks[i] = step;
        }
        this.source = source;
        this.target = target;
    }

    public long[] resolve() {
        AtomicReference<Long> result = new AtomicReference<>();
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            Long val = match(source, sourceMap, targetMap);
            if (val != null) {
                result.set(val);

            }
            LockSupport.unpark(thread);

        }).start();
        new Thread(() -> {
            Long val = match(target, targetMap, sourceMap);
            if (val != null) {
                result.set(val);

            }
            LockSupport.unpark(thread);

        }).start();

        LockSupport.park();
        if (result.get() == null) {
            return null;
        }
        LinkedList<Long> resultDeque = new LinkedList<>();
        resultDeque.offerFirst(result.get());
        Long last;
        while ((last = sourceMap.get(resultDeque.peekFirst())) != null) {
            resultDeque.offerFirst(last);
        }
        while ((last = targetMap.get(resultDeque.peekLast())) != null) {
            resultDeque.offerLast(last);
        }
        long[] steps = new long[resultDeque.size()];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = resultDeque.pollFirst();
        }
        return steps;
    }

    public String resolve(long[] steps) {
        int len = steps.length - 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            long mask = (steps[i] ^ steps[i + 1]) & this.mask;
            builder.append(format(steps[i], length))
                    .append(" ~ ").append(format(mask, length))
                    .append(" = ").append(format(steps[i + 1], length))
                    .append(" @ ");
            for (int j = 0; j < this.length; j++) {
                if (mask == masks[j]) {
                    builder.append(j + 1);
                    break;
                }
            }
            builder.append('\n');
        }
        builder.append(format(steps[len], length));
        return builder.toString();

    }

    public Long match(long val, Map<Long, Long> souMap, Map<Long, Long> tarMap) {
        Queue<Long> process = new LinkedList<>();
        souMap.put(val, null);
        process.add(val);
        Long valObj;
        while (run && (valObj = process.poll()) != null) {

            for (long step : masks) {
                Long valCalc = (valObj ^ step) & mask;
                if (!souMap.containsKey(valCalc)) { //已经存在
                    souMap.put(valCalc, valObj);
                    process.offer(valCalc);
                }
                if (tarMap.containsKey(valCalc)) {

                    run = false;
                    return valCalc;
                }

            }
        }

        return null;

    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("start with: ").append(format(this.source, length))
                .append("\nend with: ").append(format(this.target, length))
                .append("\nmasks: ").append(format(this.mask, length))
                .append("\nmasks: ");
        for (long st : masks) {
            builder.append(format(st, length)).append(" ");
        }
        return builder.toString();
    }

    public void stop() {
        this.run = false;
    }
}

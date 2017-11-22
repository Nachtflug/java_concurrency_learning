package jdk8;

import java.util.concurrent.atomic.*;

public class AtomicUpdate {

    public static void main(String[] args) {
        AtomicLong atomicLong = new AtomicLong(0);

        //CAS inside
        atomicLong.updateAndGet(x -> Math.max(x, 88));
        atomicLong.accumulateAndGet(88, Math::max);

        // Separate summands into cells. Number of cells increases when threads visiting goes up.
        // Each cell updates its inner value by CAS. When result is needed, sums cells' value up.
        LongAdder adder = new LongAdder();
        adder.increment();
        adder.add(233L);

        LongAccumulator accumulator = new LongAccumulator(Long::max, 0);
        accumulator.accumulate(12);
        accumulator.accumulate(-12);

        // Those two are equivalent
        DoubleAccumulator doubleAccumulator = new DoubleAccumulator(Double::sum, .0);
        DoubleAdder doubleAdder = new DoubleAdder();

    }

}

package com.heroku.java.util;

import org.springframework.stereotype.Component;

@Component
public class FibonacciCreator {
    public int[] createFibonacciSequence(int lvl) {
        if (lvl <= 0) {
            throw new IllegalArgumentException("lvl parameter should be above 0");
        } else if (lvl == 1) {
            int[] fibo = new int[lvl];
            fibo[0] = 1;
            return fibo;
        } else {
            int[] fibo = new int[lvl];
            fibo[0] = 0;
            fibo[1] = 1;
            for (int i = 2; i < lvl; i++) {
                fibo[i] = fibo[i - 1] + fibo[i - 2];
            }
            fibo[0] = 1;
            return fibo;
        }
    }
}

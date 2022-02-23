package com.nowcoder.community;

public class Test {
    @org.junit.Test
    public void oder() {
        int NCPUS = Runtime.getRuntime().availableProcessors();
        System.out.println(NCPUS);
        String ip = "123";
        String now = "sss";
        String target = "ddd";
        String name = "刘运姣";
        System.out.println(String.format("用户[%s],在[%s],访问了[%s],你是谁[%s]", ip, now, target, name));

        long res = getsum(3);
        System.out.println(res);
        int res2 = sum(65536);
        System.out.println(res2);
    }

    public static long getsum(long a) {
        if (a == 1) return 1;
        else
            return getsum(a - 1) * a;
    }

    public static int sum(int n) {
        int sum = 0;
        while(n!=1){
            n=n/2;
            sum++;
        }
        return  sum;
    }

}

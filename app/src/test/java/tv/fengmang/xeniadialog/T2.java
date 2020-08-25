package tv.fengmang.xeniadialog;

import org.junit.Test;

public class T2 {

    @Test
    public void reverse() {
        int data = 8743;
        int temp = 0;

        System.out.println("data is:" + data);
        do {
            System.out.println("--------------------------");
            temp = temp * 10 + data % 10;
            System.out.println("data is:" + data);
            System.out.println("temp is:" + temp);
        } while ((data = data / 10) != 0);
        if (temp > Integer.MAX_VALUE || temp < Integer.MIN_VALUE) {
            return;
        }
        System.out.println("after reverse,result is:" + temp);
    }

    @Test
    public void optRemainder() {
        int data = 8743;
        int x = data % 10;
        System.out.println("求余,x is:" + x);
    }

    @Test
    public void optDevide() {
        int data = 8743;
        int x = data / 10;
        System.out.println("求除数,x is:" + x);
    }
}

public class ThreadTest {
    public static void main(String[] args) {
        Thread t1 = new test1();
        Thread t2 = new test2();
        Thread t3 = new test3();
        t1.start();
        t2.start();
        t3.start();
    }
}
// 编写一个程序，开启 3 个线程，这三个线程的 ID 分别为 A、B、C，每个线程将自己的 ID 在屏幕上打印 10 遍，要求输出的结果必须按顺序显示。
//创建3个线程，并根据打印内容，设定每个线程sleep时间
class test1 extends Thread {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i <= 9; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("A");
        }
    }
}
class test2 extends Thread {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i <= 9; i++) {
            try {
                Thread.sleep(105);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("B");
        }
    }
}

class test3 extends Thread {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i <= 9; i++) {
            try {
                Thread.sleep(110);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("C");
        }
    }
}

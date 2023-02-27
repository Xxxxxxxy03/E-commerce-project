import java.util.ArrayList;

public class ThreadTest2 {
    //创建集合，存储生产商品个数
    private static ArrayList<Integer> product = new ArrayList<>();

    //创建锁对象
    private static Object obj = new Object();

    public static void main(String[] args) {
        startProduct(2, 8, 3);
    }

    private static void startProduct(int consumerCount, int produceSpeed, int consumeSpeed) {

        //    启动生产者
        new Producer(produceSpeed).start();

        //启动消费者
        for (int i = 1; i <= consumerCount; i++) {
            new Consumer(i,consumeSpeed).start();
        }
    }

    //生产者
    private static class Producer extends Thread {
        int n;//生产速度
        int i;//生产者生产商品个数

        public Producer(int n) {
            this.n = n;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    //休眠时间= 秒 / 生产速度
                    Thread.sleep(1000 / n);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (obj) {
                    if (product.size() == 20) {
                        try {
                            System.out.println("当前剩余20件商品-------------------------------------------------------------------------------");
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        int num = ++i;
                        product.add(num);
                        System.out.println("生产了：" + num + "件商品");
                        obj.notifyAll();
                    }
                }
            }
        }
    }

    private static class Consumer extends Thread {
        int n;//消费者编号
        int speed;//消费速度

        public Consumer(int n ,int speed) {
            this.n = n;
            this.speed = speed;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(1000 / speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (obj) {
                    if (product.size() == 0) {
                        try {
                            obj.wait();
                            System.out.println("商品消费完");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Integer a = product.remove(0);
                        System.out.println("消费者" + n + "消费商品" + a);
                        System.out.println("还剩"+ product.size()+"件商品");
                        obj.notifyAll();
                    }
                }
            }
        }
    }
}


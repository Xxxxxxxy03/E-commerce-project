public class TestTimes {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println(System.currentTimeMillis());
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

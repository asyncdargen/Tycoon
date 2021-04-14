import java.util.ArrayList;
import java.util.List;

public class Test {


    public static void main(String[] args) throws Throwable {
        List<Test> tests = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tests.add(new Test());
        }
        long start = System.currentTimeMillis();
        tests.forEach(System.out::println);
        System.out.println(System.currentTimeMillis() - start);
    }
}

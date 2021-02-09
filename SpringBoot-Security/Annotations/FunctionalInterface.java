package eu.europa.ec.sep.redress.rest;

import com.google.common.util.concurrent.Uninterruptibles;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalInterfaceTest {

    @Test
    void computeIfAbsentTest() {

        Map<String, Integer> nameMap = new HashMap<>();
        nameMap.put("John", 13);
        Integer value = nameMap.computeIfAbsent("John", String::length);
        System.out.println(value);
    }

    @Test
    void composeTest() {
        Function<Integer, String> intToString = s -> {
            System.out.println("inside toString");
            return s.toString();
        };
        Function<String, String> quote = s -> {
            System.out.println("added quotes");
            return "'" + s + "'";
        };

        Function<Integer, String> quoteIntToString = quote.compose(intToString);

        assertEquals("'5'", quoteIntToString.apply(5));
    }


    @FunctionalInterface
    public interface ShortToByteFunction {

        byte applyAsByte(short s);

    }

    public byte[] transformArray(short[] array, ShortToByteFunction function) {
        byte[] transformedArray = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            transformedArray[i] = function.applyAsByte(array[i]);
        }
        return transformedArray;
    }

    @Test
    void transformedArrayTest() {

        short[] array = {(short) 1, (short) 2, (short) 3};
        byte[] transformedArray = transformArray(array, s -> (byte) (s * 2));

        byte[] expectedArray = {(byte) 2, (byte) 4, (byte) 6};
        assertArrayEquals(expectedArray, transformedArray);
    }


    @Test
    void replaceALlTest() {
        Map<String, Integer> salaries = new HashMap<>();
        salaries.put("John", 40000);
        salaries.put("Freddy", 30000);
        salaries.put("Samuel", 50000);

        salaries.replaceAll((name, oldValue) ->
                name.equals("Freddy") ? oldValue : oldValue + 10000);


    }

    public double squareLazy(Supplier<Double> lazyValue) {
        double pow = Math.pow(lazyValue.get(), 2);
        System.out.println("pow: " + pow);
        return pow;
    }

    @Test
    void suppliersTest() {
        Supplier<Double> lazyValue = () -> {
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            System.out.println("time's up!");
            return 9d;
        };

        Double valueSquared = squareLazy(lazyValue);
        System.out.println("valueSquared + " + valueSquared);

    }

    @Test
        //warning infinite loop with generate
    void FibonacciTest() {
        int[] fibs = {0, 1};
        Stream<Integer> fibonacci = Stream.generate(() -> {
            int result = fibs[1];
            System.out.println("1 - result: " + result);
            int fib3 = fibs[0] + fibs[1];
            fibs[0] = fibs[1];
            fibs[1] = fib3;
            System.out.println("2 - result: " + result);
            return result;
        });

        System.out.println("fibonacci.count");
        fibonacci.count();
    }


    @Test
    void consumerTest() {
        List<String> names = Arrays.asList("John", "Freddy", "Samuel");
        names.forEach(name -> System.out.println("Hello, " + name));
    }

    @Test
    void biConsumerTest() {
        Map<String, Integer> ages = new HashMap<>();
        ages.put("John", 25);
        ages.put("Freddy", 24);
        ages.put("Samuel", 30);

        ages.forEach((name, age) -> System.out.println(name + " is " + age + " years old"));
    }

    @Test
    void predicatesTest() {
        List<String> names = Arrays.asList("Angela", "Aaron", "Bob", "Claire", "David");

        List<String> namesWithA = names.stream()
                .filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());
    }

    @Test
    void operatorsTest() {
        List<String> names = Arrays.asList("bob", "josh", "megan");
        //Lambda
        names.replaceAll(name -> name.toUpperCase());
        //OR use a method reference
        names.replaceAll(String::toUpperCase);
    }

    @Test
    void binaryOperatorsTest() {
        List<Integer> values = Arrays.asList(3, 5, 8, 9, 12);

        int sum = values.stream()
                .reduce(0, (i1, i2) -> i1 + i2);
    }
}

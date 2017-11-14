package concurrency.collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import util.IterableUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class SkipListMap {

    @AllArgsConstructor @Getter
    static class Contact {
        private String name;
        private String phone;
    }

    @AllArgsConstructor
    static class Task implements Runnable {
        private String id;
        private ConcurrentSkipListMap<String, Contact> map;

        @Override
        public void run() {
            IterableUtils.genIdxList(0, 100).stream()
                    .map(i -> new Contact(id, (i + 1000) + ""))
                    .forEach(c -> map.put(id + c.getPhone(), c));
        }
    }

    public static void main(String[] args) {
        ConcurrentSkipListMap<String, Contact> map = new ConcurrentSkipListMap<>();
        List<Thread> threads = IterableUtils.genIdxList('A', 'Z').stream()
                .map(c -> new Task(String.valueOf((char) c.byteValue()), map))
                .map(Thread::new)
                .collect(Collectors.toList());
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.printf("Main: Size of the map: %d\n", map.size());
        Contact first = map.firstEntry().getValue();
        System.out.printf("Main: First Entry: %s: %s\n", first.getName(), first.getPhone());
        Contact last = map.lastEntry().getValue();
        System.out.printf("Main: Last Entry: %s: %s\n", last.getName(), last.getPhone());
        System.out.printf("Main: Submap from A1096 to B1002:\n");
        ConcurrentNavigableMap<String, Contact> subMap = map.subMap("A1096", "B1002");
//        subMap.navigableKeySet().stream()
//                .map(subMap::get)
//                .forEach(c -> System.out.printf("%s: %s\n", c.getName(), c.getPhone()));

        subMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .forEach(c -> System.out.printf("%s: %s\n", c.getName(), c.getPhone()));

    }

}

package algorithem;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import util.IterableUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Getter
@Setter
public class Huffman {

    private Map<Character, String> table;
    private Map<String, Character> rTable;
    private FrequencyNode root;

    @Getter
    private class FrequencyNode {
        char chl;
        int times;

        FrequencyNode left, right;

        FrequencyNode(int chl) {
            this.chl = (char) chl;
            times = 1;
        }

        FrequencyNode(FrequencyNode f1, FrequencyNode f2) {
            times = f1.times + f2.times;
            chl = f1.chl;
        }

        FrequencyNode associateTo(FrequencyNode f) {
            FrequencyNode ret = new FrequencyNode(this, f);
            ret.left = this;
            ret.right = f;
            return ret;
        }

    }

    private PriorityQueue<FrequencyNode> countChar(CharSequence str) {

        PriorityQueue<FrequencyNode> queue = new PriorityQueue<>(Comparator.comparingInt(f -> f.times));

        str.chars()
                .mapToObj(FrequencyNode::new)
                .collect(groupingBy(FrequencyNode::getChl,
                                    reducing(FrequencyNode::new)))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(queue::add);

        return queue;

    }

    private FrequencyNode buildTree(PriorityQueue<FrequencyNode> queue) {
        while (queue.size() > 1) {
            queue.add(queue.poll().associateTo(queue.poll()));
        }
        return queue.poll();
    }

    private void buildTable(FrequencyNode root) {
        table = new HashMap<>();
        buildTableRecursive(root, "");
    }

    private void buildTableRecursive(FrequencyNode node, String path) {
        if (node.left == null) {
            table.put(node.chl, path);
        } else {
            buildTableRecursive(node.left, path + "0");
            buildTableRecursive(node.right, path + "1");
        }
    }

    public List<Byte> encode(CharSequence str) {

        PriorityQueue<FrequencyNode> charCount = countChar(str);
        root = buildTree(charCount);
        buildTable(root);

        String[] tokens = IterableUtils.genIdxList(0, str.length())
                .map(str::charAt)
                .map(table::get)
                .reduce(String::concat)
                .orElse("")
                .split("(?<=^(\\d{7}){0,5000})");

        List<Byte> list = IterableUtils.genIdxList(0, tokens.length)
                .map(idx -> Byte.parseByte(tokens[idx], 2))
                .collect(Collectors.toList());
        list.add(0, (byte) (7 - tokens[tokens.length - 1].length()));  //tail padding
        return list;
    }

    public String decode(List<Byte> bytes) {

        if (table == null)
            throw new IllegalStateException("Huffman table is not set.");
        if (rTable == null)
            rTable = table.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        int tailPadding = bytes.get(0);
        bytes.remove(0);
        String byteStr = bytes.stream()
                .map(b -> Integer.toString(b, 2))
                .map(s -> Strings.padStart(s, 7, '0'))
                .reduce(String::concat)
                .orElse("");
        byteStr = byteStr.substring(0, 7 * bytes.size() - 7)
                + byteStr.substring(7 * bytes.size() - 7 + tailPadding, byteStr.length());

        String token = "";
        String ret = "";
        for (char c : byteStr.toCharArray()) {
            token += c;
            if (rTable.get(token) == null)
                continue;
            ret += rTable.get(token);
            token = "";
        }

        return ret;
    }

    public static void main(String[] args) {
        String test = "八百标兵奔北坡，北坡炮兵并排跑，炮兵怕把标兵碰，标兵怕碰炮兵炮。";
        Huffman h = new Huffman();
        List<Byte> encoded = h.encode(test);
        System.out.println(h.decode(encoded));
    }

}

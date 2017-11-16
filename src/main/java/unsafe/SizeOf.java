package unsafe;

import org.openjdk.jol.info.ClassLayout;
import util.UnsafeUtils;

public class SizeOf {

    private String s = "welcome";
    private String d = "df";

    public static void main(String[] args) {
        SizeOf sizeOf = new SizeOf();

        System.out.println(UnsafeUtils.sizeOf(sizeOf));
        System.out.println(ClassLayout.parseInstance(sizeOf).toPrintable());
    }


}

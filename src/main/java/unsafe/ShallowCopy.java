package unsafe;

import util.UnsafeUtils;

public class ShallowCopy {

    private int a = 2;


    public static void main(String[] args) {
        ShallowCopy shallowCopy = new ShallowCopy();
        ShallowCopy copy = (ShallowCopy) UnsafeUtils.shallowCopy(shallowCopy);
        copy.a = 6;
        System.out.println(copy.a);

    }

}

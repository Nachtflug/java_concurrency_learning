package jdk8;

public class DefaultMethodConfliction {

    interface A {
        default String laugh() {
            return "A";
        }
    }

    interface B {
        default String laugh() {
            return "B";
        }
    }

    interface C extends A {
        default String laugh() {
            return "C";
        }
    }

    interface D {
        String laugh();
    }

    abstract static class F {
        public String laugh() {
            return "F";
        }
    }

    //Conflicts situation
    static class AB implements A, B {

        @Override
        public String laugh() {
            return "AB";
        }
    }

    static class AD implements A, D {

        @Override
        public String laugh() {
            return "AD";
        }
    }

    // super interface wins
    static class AC implements A, C { }

    // class wins
    static class AF extends F implements A, B { }

    public static void main(String[] args) {
        System.out.println(new AC().laugh());
        System.out.println(new AF().laugh());
    }
}

package android.util;

public final class Log {

    public static int v(String tag, String msg) {
        print("v", tag, msg);
        return 0;
    }

    public static int d(String tag, String msg) {
        print("d", tag, msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        print("i", tag, msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        print("w", tag, msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        print("e", tag, msg);
        return 0;
    }

    private static void print(String level, String tag, String msg) {
        System.out.println(String.format("%s/%s: %s", level, tag, msg));
    }
}

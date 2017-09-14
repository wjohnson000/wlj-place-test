package std.wlj.xlit;

public class GenerateGlyph {
    public static void main(String...args) {
        char ch01 = 0x179B;
        char ch02 = 0x17C5;

        StringBuilder buff = new StringBuilder();
        buff.append(ch01).append(ch02);
        String what = buff.toString();
        System.out.println(what.length() + " --> " + what);
    }
}

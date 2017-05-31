package std.wlj.general;

public class TestGeneric {
	public static void main(String... args) {
		Generic<String> sg = new Generic<>("Wayne");
		System.out.println("SG: " + sg);

		Generic<String> sgNull = new Generic<>();
		System.out.println("SG: " + sgNull);
	}
}

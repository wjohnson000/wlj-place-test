package std.wlj.general;

public class RawByteClassLoader extends ClassLoader {

    public Class<?> createFrom(String name, byte[] rawBytes) {
        return this.defineClass(name, rawBytes, 0, rawBytes.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] rawBytes = new byte[0];

        return this.defineClass(name, rawBytes, 0, rawBytes.length);
    }
}

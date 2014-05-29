package std.wlj.memory;

import java.io.*;


public class HowMuchMemory {
    static class One implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    static class Two implements Serializable {
        private static final long serialVersionUID = 1L;
        String str01 = "";
    }

    static class Tre implements Serializable {
        private static final long serialVersionUID = 1L;
        String str01 = "A";
    }

    static class For implements Serializable {
        private static final long serialVersionUID = 1L;
        String str01 = "AB";
    }

    static class Fiv implements Serializable {
        private static final long serialVersionUID = 1L;
        String str01 = "";
        String str02 = "";
    }

    static class Six implements Serializable {
        private static final long serialVersionUID = 1L;
        String str01 = "A";
        String str02 = "B";
    }

    public static void main(String... args) throws Exception {
        serializeIt(new One(), "C:/temp/xx-one.ser");
        serializeIt(new Two(), "C:/temp/xx-two.ser");
        serializeIt(new Tre(), "C:/temp/xx-tre.ser");
        serializeIt(new For(), "C:/temp/xx-for.ser");
        serializeIt(new Fiv(), "C:/temp/xx-fiv.ser");
        serializeIt(new Six(), "C:/temp/xx-six.ser");
    }

    private static void serializeIt(Object what, String fileName) throws Exception {
        OutputStream file = new FileOutputStream(fileName);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);
        output.writeObject(what);
        output.close();

        File fileX = new File(fileName);
        System.out.println("File: " + fileX.getAbsolutePath() + " --> " + fileX.length());
    }
}

package std.wlj.general;

public class GenerateUcodesForNames {

    static int[] displayNameOK = {
        33,
        38,
        39,
        45,
        48,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        56,
        57,
        1470,
        3666,
        3667,
        3669,
        3851,
        6113,
        6114,
        6115,
        6116,
        6117,
        6118,
        6119,
        6120,
        6121,
    };

    static int[] variantNameBad = {
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        36,
        46,
        58,
        59,
        60,
        61,
        62,
        63,
        64,
        92,
        123,
        125,
        126,
        127,
        160,
        191,
        1425,
        1523,
        8206,
        8209,
        8211,
        8226,
        8249,
        8304,
        12289,
        12539,
        65056,
        65057
    };

    public static void main(String...args) {
        for (int charCode : displayNameOK) {
            String hex = Integer.toHexString(charCode);
            String prefix = "0000".substring(0, 4-hex.length());
            System.out.println("display.ok.u" + prefix+hex + " = \\U" + prefix + hex);
        }

        for (int charCode : variantNameBad) {
            String hex = Integer.toHexString(charCode);
            String prefix = "0000".substring(0, 4-hex.length());
            System.out.println("variant.illegal.u" + prefix+hex + " = \\U" + prefix + hex);
        }
    }
}

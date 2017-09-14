package std.wlj.unicode;

import java.lang.Character.UnicodeBlock;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class UnicodeRanges {

    static Map<Byte, String> typeName = new HashMap<>();
    static {
        typeName.put(Character.UNASSIGNED, "UNASSIGNED");
        typeName.put(Character.UPPERCASE_LETTER, "UPPERCASE_LETTER");
        typeName.put(Character.LOWERCASE_LETTER, "LOWERCASE_LETTER");
        typeName.put(Character.TITLECASE_LETTER, "TITLECASE_LETTER");
        typeName.put(Character.MODIFIER_LETTER, "MODIFIER_LETTER");
        typeName.put(Character.OTHER_LETTER, "OTHER_LETTER");
        typeName.put(Character.NON_SPACING_MARK, "NON_SPACING_MARK");
        typeName.put(Character.ENCLOSING_MARK, "ENCLOSING_MARK");
        typeName.put(Character.COMBINING_SPACING_MARK, "COMBINING_SPACING_MARK");
        typeName.put(Character.DECIMAL_DIGIT_NUMBER, "DECIMAL_DIGIT_NUMBER");
        typeName.put(Character.LETTER_NUMBER, "LETTER_NUMBER");
        typeName.put(Character.OTHER_NUMBER, "OTHER_NUMBER");
        typeName.put(Character.SPACE_SEPARATOR, "SPACE_SEPARATOR");
        typeName.put(Character.LINE_SEPARATOR, "LINE_SEPARATOR");
        typeName.put(Character.PARAGRAPH_SEPARATOR, "PARAGRAPH_SEPARATOR");
        typeName.put(Character.CONTROL, "CONTROL");
        typeName.put(Character.FORMAT, "FORMAT");
        typeName.put(Character.PRIVATE_USE, "PRIVATE_USE");
        typeName.put(Character.SURROGATE, "SURROGATE");
        typeName.put(Character.DASH_PUNCTUATION, "DASH_PUNCTUATION");
        typeName.put(Character.START_PUNCTUATION, "START_PUNCTUATION");
        typeName.put(Character.END_PUNCTUATION, "END_PUNCTUATION");
        typeName.put(Character.CONNECTOR_PUNCTUATION, "CONNECTOR_PUNCTUATION");
        typeName.put(Character.OTHER_PUNCTUATION, "OTHER_PUNCTUATION");
        typeName.put(Character.MATH_SYMBOL, "MATH_SYMBOL");
        typeName.put(Character.CURRENCY_SYMBOL, "CURRENCY_SYMBOL");
        typeName.put(Character.MODIFIER_SYMBOL, "MODIFIER_SYMBOL");
        typeName.put(Character.OTHER_SYMBOL, "OTHER_SYMBOL");
        typeName.put(Character.INITIAL_QUOTE_PUNCTUATION, "INITIAL_QUOTE_PUNCTUATION");
        typeName.put(Character.FINAL_QUOTE_PUNCTUATION, "FINAL_QUOTE_PUNCTUATION");
    }

    public static void main(String... args) {
        char low  = '\u0180';
        char high = '\uFFFF';
        System.out.println("Range: " + (int)low + " to " + (int)high);

        UnicodeBlock block = null;
        for (int ndx=low;  ndx<=high;  ndx++) {
            char ch = (char)ndx;
            if (Character.getName(ch) != null  &&  ! Character.isAlphabetic(ch)  &&
                    Character.getType(ch) != Character.PRIVATE_USE  &&  Character.getType(ch) != Character.MATH_SYMBOL  &&
                    Character.getType(ch) != Character.OTHER_SYMBOL  &&  Character.getType(ch) != Character.OTHER_NUMBER  &&
                    Character.getType(ch) != Character.CURRENCY_SYMBOL  &&  Character.getType(ch) != Character.SURROGATE) {
                if (block != UnicodeBlock.of(ch)) {
                    block = UnicodeBlock.of(ch);
                    System.out.println("\n\n");
                }
                System.out.println(getUChars(ch) +
                    "\t" + (int)ch +
                    "\t" + ch +
                    "\t" + typeName.getOrDefault((byte)Character.getType(ch), "UNKNOWN") +
                    "\t" + UnicodeBlock.of(ch) +
                    "\t" + Character.getName(ch) +
                    "\t" + Character.isAlphabetic(ch) +
                    "\t" + Character.isLetter(ch));
            }
        }
    }

    static String getUChars(char ch) {
        String hex = Integer.toHexString(ch).toUpperCase();
        return "U+" + StringUtils.leftPad(hex, 4, "0");
    }
}
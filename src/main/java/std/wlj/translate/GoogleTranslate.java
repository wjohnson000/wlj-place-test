/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.translate;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/**
 * NOTE: this requires a google "API key", which I don't have ... I can sign up for a free
 * account if I'm willing to give them a credit card, which they promise won't be charged for
 * three month.  Hmmmmmmmmm ...
 * 
 * @author wjohnson000
 *
 */
public class GoogleTranslate {

    static String TEXT = "Hello and happy birthday to you!";

    public static void main(String... args) {
        // Instantiates a client
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        // The text to translate
        String text = "Hello, world!";

        // Translates some text into Russian
        Translation translation =
            translate.translate(
                TEXT, TranslateOption.sourceLanguage("en"), TranslateOption.targetLanguage("ru"));

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", translation.getTranslatedText());

    }
}

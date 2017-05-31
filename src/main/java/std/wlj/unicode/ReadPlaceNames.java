package std.wlj.unicode;

import java.sql.*;
import java.util.*;

import std.wlj.datasource.DbConnectionManager;

public class ReadPlaceNames {

	private static class UnicodeRange {
		int startChar;
		int endChar;
		String name;

		UnicodeRange(int startChar, int endChar, String name) {
			this.startChar = startChar;
			this.endChar   = endChar;
			this.name      = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final String nameQuery = "SELECT * FROM place_name WHERE place_id = ";

	private static Connection conn;

	private static UnicodeRange[] allRanges = {
			new UnicodeRange(0x0000, 0x007F, "Basic Latin"),
			new UnicodeRange(0x0080, 0x00FF, "Latin-1 Supplement"),
			new UnicodeRange(0x0100, 0x017F, "Latin Extended-A"),
			new UnicodeRange(0x0180, 0x024F, "Latin Extended-B"),
			new UnicodeRange(0x0250, 0x02AF, "IPA Extensions"),
			new UnicodeRange(0x02B0, 0x02FF, "Spacing Modifier Letters"),
			new UnicodeRange(0x0300, 0x036F, "Combining Diacritical Marks"),
			new UnicodeRange(0x0370, 0x03FF, "Greek"),
			new UnicodeRange(0x0400, 0x04FF, "Cyrillic"),
			new UnicodeRange(0x0530, 0x058F, "Armenian"),
			new UnicodeRange(0x0590, 0x05FF, "Hebrew"),
			new UnicodeRange(0x0600, 0x06FF, "Arabic"),
			new UnicodeRange(0x0700, 0x074F, "Syriac"),
			new UnicodeRange(0x0780, 0x07BF, "Thaana"),
			new UnicodeRange(0x0900, 0x097F, "Devanagari"),
			new UnicodeRange(0x0980, 0x09FF, "Bengali"),
			new UnicodeRange(0x0A00, 0x0A7F, "Gurmukhi"),
			new UnicodeRange(0x0A80, 0x0AFF, "Gujarati"),
			new UnicodeRange(0x0B00, 0x0B7F, "Oriya"),
			new UnicodeRange(0x0B80, 0x0BFF, "Tamil"),
			new UnicodeRange(0x0C00, 0x0C7F, "Telugu"),
			new UnicodeRange(0x0C80, 0x0CFF, "Kannada"),
			new UnicodeRange(0x0D00, 0x0D7F, "Malayalam"),
			new UnicodeRange(0x0D80, 0x0DFF, "Sinhala"),
			new UnicodeRange(0x0E00, 0x0E7F, "Thai"),
			new UnicodeRange(0x0E80, 0x0EFF, "Lao"),
			new UnicodeRange(0x0F00, 0x0FFF, "Tibetan"),
			new UnicodeRange(0x1000, 0x109F, "Myanmar"),
			new UnicodeRange(0x10A0, 0x10FF, "Georgian"),
			new UnicodeRange(0x1100, 0x11FF, "Hangul Jamo"),
			new UnicodeRange(0x1200, 0x137F, "Ethiopic"),
			new UnicodeRange(0x13A0, 0x13FF, "Cherokee"),
			new UnicodeRange(0x1400, 0x167F, "Unified Canadian Aboriginal Syllabics"),
			new UnicodeRange(0x1680, 0x169F, "Ogham"),
			new UnicodeRange(0x16A0, 0x16FF, "Runic"),
			new UnicodeRange(0x1780, 0x17FF, "Khmer"),
			new UnicodeRange(0x1800, 0x18AF, "Mongolian"),
			new UnicodeRange(0x1E00, 0x1EFF, "Latin Extended Additional"),
			new UnicodeRange(0x1F00, 0x1FFF, "Greek Extended"),
			new UnicodeRange(0x2000, 0x206F, "General Punctuation"),
			new UnicodeRange(0x2070, 0x209F, "Superscripts and Subscripts"),
			new UnicodeRange(0x20A0, 0x20CF, "Currency Symbols"),
			new UnicodeRange(0x20D0, 0x20FF, "Combining Marks for Symbols"),
			new UnicodeRange(0x2100, 0x214F, "Letterlike Symbols"),
			new UnicodeRange(0x2150, 0x218F, "Number Forms"),
			new UnicodeRange(0x2190, 0x21FF, "Arrows"),
			new UnicodeRange(0x2200, 0x22FF, "Mathematical Operators"),
			new UnicodeRange(0x2300, 0x23FF, "Miscellaneous Technical"),
			new UnicodeRange(0x2400, 0x243F, "Control Pictures"),
			new UnicodeRange(0x2440, 0x245F, "Optical Character Recognition"),
			new UnicodeRange(0x2460, 0x24FF, "Enclosed Alphanumerics"),
			new UnicodeRange(0x2500, 0x257F, "Box Drawing"),
			new UnicodeRange(0x2580, 0x259F, "Block Elements"),
			new UnicodeRange(0x25A0, 0x25FF, "Geometric Shapes"),
			new UnicodeRange(0x2600, 0x26FF, "Miscellaneous Symbols"),
			new UnicodeRange(0x2700, 0x27BF, "Dingbats"),
			new UnicodeRange(0x2800, 0x28FF, "Braille Patterns"),
			new UnicodeRange(0x2E80, 0x2EFF, "CJK Radicals Supplement"),
			new UnicodeRange(0x2F00, 0x2FDF, "Kangxi Radicals"),
			new UnicodeRange(0x2FF0, 0x2FFF, "Ideographic Description Characters"),
			new UnicodeRange(0x3000, 0x303F, "CJK Symbols and Punctuation"),
			new UnicodeRange(0x3040, 0x309F, "Hiragana"),
			new UnicodeRange(0x30A0, 0x30FF, "Katakana"),
			new UnicodeRange(0x3100, 0x312F, "Bopomofo"),
			new UnicodeRange(0x3130, 0x318F, "Hangul Compatibility Jamo"),
			new UnicodeRange(0x3190, 0x319F, "Kanbun"),
			new UnicodeRange(0x31A0, 0x31BF, "Bopomofo Extended"),
			new UnicodeRange(0x3200, 0x32FF, "Enclosed CJK Letters and Months"),
			new UnicodeRange(0x3300, 0x33FF, "CJK Compatibility"),
			new UnicodeRange(0x3400, 0x4DB5, "CJK Unified Ideographs Extension A"),
			new UnicodeRange(0x4E00, 0x9FFF, "CJK Unified Ideographs"),
			new UnicodeRange(0xA000, 0xA48F, "i Syllables"),
			new UnicodeRange(0xA490, 0xA4CF, "i Radicals"),
			new UnicodeRange(0xAC00, 0xD7A3, "Hangul Syllables"),
			new UnicodeRange(0xD800, 0xDB7F, "High Surrogates"),
			new UnicodeRange(0xDB80, 0xDBFF, "High Private Use Surrogates"),
			new UnicodeRange(0xDC00, 0xDFFF, "Low Surrogates"),
			new UnicodeRange(0xE000, 0xF8FF, "Private Use"),
			new UnicodeRange(0xF900, 0xFAFF, "CJK Compatibility Ideographs"),
			new UnicodeRange(0xFB00, 0xFB4F, "Alphabetic Presentation Forms"),
			new UnicodeRange(0xFB50, 0xFDFF, "Arabic Presentation Forms-A"),
			new UnicodeRange(0xFE20, 0xFE2F, "Combining Half Marks"),
			new UnicodeRange(0xFE30, 0xFE4F, "CJK Compatibility Forms"),
			new UnicodeRange(0xFE50, 0xFE6F, "Small Form Variants"),
			new UnicodeRange(0xFE70, 0xFEFE, "Arabic Presentation Forms-B"),
			new UnicodeRange(0xFEFF, 0xFEFF, "Specials"),
			new UnicodeRange(0xFF00, 0xFFEF, "Halfwidth and Fullwidth Forms"),
			new UnicodeRange(0xFFF0, 0xFFFD, "Specials"),
	};

	public static void main(String... args) throws Exception {
		conn = DbConnectionManager.getConnectionAws();
		List<String> usNames = getNames(1L);
		for (String name : usNames) {
			List<UnicodeRange> nameRanges = calculateNameRanges(name);
			System.out.println(name + " --> " + nameRanges);
		}
		closeConnection();
	}

	static void closeConnection() throws Exception {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	static List<String> getNames(long placeId) {
		List<String> names = new ArrayList<>();
		try (Statement stmt = conn.createStatement();
			 ResultSet rset = stmt.executeQuery(nameQuery + placeId)) {
			while (rset.next()) {
				names.add(rset.getString("text"));
			}
		} catch(SQLException ex) {
			
		}
		return names;
	}

	private static List<UnicodeRange> calculateNameRanges(String name) {
		List<UnicodeRange> result = new ArrayList<>();
		for (char ch : name.toCharArray()) {
			for (UnicodeRange uRange : allRanges) {
				if (ch >= uRange.startChar  &&  ch <= uRange.endChar) {
					if (! result.contains(uRange)) {
						result.add(uRange);
					}
					break;
				}
			}
		}
		return result;
	}

}

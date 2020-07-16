package istat.android.base.tools;

import java.util.Locale;

public class CountryFlags {
    private static String A = getEmojiByUnicode(0x1F1E6);
    private static String B = getEmojiByUnicode(0x1F1E7);
    private static String C = getEmojiByUnicode(0x1F1E8);
    private static String D = getEmojiByUnicode(0x1F1E9);
    private static String E = getEmojiByUnicode(0x1F1EA);
    private static String F = getEmojiByUnicode(0x1F1EB);
    private static String G = getEmojiByUnicode(0x1F1EC);
    private static String H = getEmojiByUnicode(0x1F1ED);
    private static String I = getEmojiByUnicode(0x1F1EE);
    private static String J = getEmojiByUnicode(0x1F1EF);
    private static String K = getEmojiByUnicode(0x1F1F0);
    private static String L = getEmojiByUnicode(0x1F1F1);
    private static String M = getEmojiByUnicode(0x1F1F2);
    private static String N = getEmojiByUnicode(0x1F1F3);
    private static String O = getEmojiByUnicode(0x1F1F4);
    private static String P = getEmojiByUnicode(0x1F1F5);
    private static String Q = getEmojiByUnicode(0x1F1F6);
    private static String R = getEmojiByUnicode(0x1F1F7);
    private static String S = getEmojiByUnicode(0x1F1F8);
    private static String T = getEmojiByUnicode(0x1F1F9);
    private static String U = getEmojiByUnicode(0x1F1FA);
    private static String V = getEmojiByUnicode(0x1F1FB);
    private static String W = getEmojiByUnicode(0x1F1FC);
    private static String Y = getEmojiByUnicode(0x1F1FE);
    private static String Z = getEmojiByUnicode(0x1F1FF);

    private static String getCodeByCharacter(Character character) {
        String code;
        switch (Character.toUpperCase(character)) {
            case 'A':
                code = A;
                break;
            case 'B':
                code = B;
                break;
            case 'C':
                code = C;
                break;
            case 'D':
                code = D;
                break;
            case 'E':
                code = E;
                break;
            case 'F':
                code = F;
                break;
            case 'G':
                code = G;
                break;
            case 'H':
                code = H;
                break;
            case 'I':
                code = I;
                break;
            case 'J':
                code = J;
                break;
            case 'K':
                code = K;
                break;
            case 'L':
                code = L;
                break;
            case 'M':
                code = M;
                break;
            case 'N':
                code = N;
                break;
            case 'O':
                code = O;
                break;
            case 'P':
                code = P;
                break;
            case 'Q':
                code = Q;
                break;
            case 'R':
                code = R;
                break;
            case 'S':
                code = S;
                break;
            case 'T':
                code = T;
                break;
            case 'U':
                code = U;
                break;
            case 'V':
                code = V;
                break;
            case 'W':
                code = W;
                break;
            case 'Y':
                code = Y;
                break;
            case 'Z':
                code = Z;
                break;
            default:
                code = "";
                break;
        }
        return code;
    }

    private static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static String getCountryFlagByLocale(Locale locale) {
        return getCountryFlagByLocale(locale, "en");
    }

    public static String getCountryFlagByLocale(Locale locale, String defaultCode) {
        return getCountryFlagByCountryCode(locale.getLanguage(), defaultCode);
    }

    public static String getCountryFlagByCountryCode(String countryCode) {
        return getCountryFlagByCountryCode(countryCode, "en");
    }

    public static String getCountryFlagByCountryCode(String countryCode, String defaultCode) {
        if(countryCode.equalsIgnoreCase("en")){
            return "\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F";
        }
        String flag;
        if (countryCode.length() == 2) {
            flag = getCodeByCharacter(countryCode.charAt(0)) + getCodeByCharacter(countryCode.charAt(1));
        } else {
            flag = defaultCode;
        }
        return flag;
    }
}
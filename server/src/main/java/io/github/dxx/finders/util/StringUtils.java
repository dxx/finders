package io.github.dxx.finders.util;

import java.util.Objects;

/**
 * String utils.
 *
 * @author dxx
 */
public class StringUtils {

    /**
     * Judge whether string is blank.
     *
     * @param str string
     * @return true if str is only blank, otherwise false
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Judge whether all strings are blank.
     *
     * @param strs strings
     * @return true if all strings are blank, otherwise false
     */
    public static boolean isAllBlank(String... strs) {
        for (String str : strs) {
            if (isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Judge whether string is not a blank.
     *
     * @param str strings
     * @return true if str is null, empty string, otherwise false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Judge whether string is not empty.
     *
     * @param str strings
     * @return false if str is null, empty string, otherwise true
     */
    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * Judge whether string is empty.
     *
     * @param str strings
     * @return true if str is null, empty string, otherwise false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Get a string value, if empty, return default value.
     * @param str string
     * @param defaultStr default value
     * @return string value
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return StringUtils.isEmpty(str) ? defaultStr : str;
    }

    /**
     * Compare whether the two strings are equal.
     */
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    /**
     * Remove the spaces before and after the string.
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

}

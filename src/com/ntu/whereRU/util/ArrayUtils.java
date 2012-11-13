
package com.ntu.whereRU.util;

public class ArrayUtils {

    /**
     * Checks whether the value is in the array or not. Functions only with type
     * long
     * 
     * @param array the array
     * @param value the value to check
     * @return whether the value is in the array or not
     */
    public static boolean inArray(final long[] array, final long value) {

        final int arrayLength = array.length;
        for (int i = 0; i < arrayLength; i++) {
            if (array[i] == value) {
                return true;
            }
        }

        return false;
    }
}

package io.hychou.data.util;

import com.google.common.math.DoubleMath;
import io.hychou.common.exception.IllegalArgumentException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class DataUtils {
    private DataUtils() {
    }

    /**
     * If a and b are numeric strings, return positive if a>b, negative
     * if a<b, 0 if a=b. If a or b is not numeric, return a.compareTo(b).
     */
    public static int libsvmTokenCompare(String a, String b, double tolerance) {
        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);
            return DoubleMath.fuzzyCompare(da, db, tolerance);
        } catch(NumberFormatException e) {
            return a.compareTo(b);
        }
    }

    /**
     * If a and b are numeric strings, return positive if a>b, negative
     * if a<b, 0 if a=b. If a or b is not numeric, return a.compareTo(b).
     */
    public static int libsvmTokenCompare(String a, String b) {
        return libsvmTokenCompare(a, b, 0);
    }

    public static int atoi(String s) {
        return Integer.parseInt(s);
    }

    public static double atof(String s) {
        double d = Double.parseDouble(s);
        if (Double.isNaN(d) || Double.isInfinite(d))
        {
            throw new NumberFormatException("NaN or Infinity in input");
        }
        return(d);
    }

    private static String errorMessageBuilder(int lineNum, String err) {
        return String.format("line %d: %s", lineNum, err);
    }

    public static void checkData(byte[] dataBytes) throws IOException, IllegalArgumentException{
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(dataBytes))
        );
        StringJoiner errorMessageJoiner = new StringJoiner(System.lineSeparator());
        int lineNum = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lineNum++;
            checkLine(line, lineNum, errorMessageJoiner);
        }
        if(errorMessageJoiner.length() > 0){
            throw new IllegalArgumentException(errorMessageJoiner.toString());
        }
    }
    private static void checkLine(String line, int lineNum, StringJoiner errorMessageJoiner) {
        String[] nodes = line.trim().split(" ");
        if(nodes.length == 0 || nodes[0].length() == 0) {
            errorMessageJoiner.add(errorMessageBuilder(lineNum,
                    "missing label, perhaps an empty line?")
            );
            return;
        }
        try{
            checkLabel(nodes[0]);
        } catch (IllegalArgumentException e) {
            errorMessageJoiner.add(errorMessageBuilder(lineNum, e.getMessage()));
        }
        // check features
        int previousIndex = -1;
        for(int i = 1; i < nodes.length; i++) {
            try {
                previousIndex = checkFeature(nodes, i, previousIndex);
            } catch (IllegalArgumentException e) {
                errorMessageJoiner.add(errorMessageBuilder(lineNum, e.getMessage()));
            }
        }

    }
    private static void checkLabel(String label) throws IllegalArgumentException {
        if (label.contains(",")) {
            // multi-label form
            try {
                for (String l : label.split(",")) {
                    atof(l);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("label '%s' is not a valid multi-label form", label)
                );
            }
        } else {
            try {
                atof(label);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        String.format("label '%s' is not a number", label)
                );
            }
        }
    }
    private static int checkFeature(String[] nodes, int i, int preIndex) throws IllegalArgumentException {
        try {
            String[] indexValuePair = nodes[i].split(":");
            int index = Integer.parseInt(indexValuePair[0]);
            atof(indexValuePair[1]);

            if(index < 0) {
                throw new IllegalArgumentException(
                        String.format("feature index must be non-negative; wrong feature '%s'", nodes[i])
                );
            } else if(index <= preIndex) {
                throw new IllegalArgumentException(
                        String.format("feature indices must be in an ascending order, previous/current features '%s' '%s'", nodes[i], nodes[i-1])
                );
            }
            return index;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("feature '%s' not an <index>:<value> pair, <index> integer, <value> real number ", nodes[i])
            );
        }
    }
}

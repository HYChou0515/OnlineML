package io.hychou.data.entity;

import io.hychou.common.AbstractDataStructure;
import io.hychou.common.Constant;
import io.hychou.common.SignificantField;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataPoint extends AbstractDataStructure {
    private Double y;
    private List<IndexValue> x;

    public DataPoint() {
    }

    public DataPoint(Double y, List<IndexValue> x) {
        this.y = y;
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public List<IndexValue> getX() {
        return x;
    }

    public void setX(List<IndexValue> x) {
        this.x = x;
    }

    public static DataPoint parseDataPoint(String line) {
        if (line == null) {
            throw new NullPointerException("Unable to parse null string");
        }

        // split space and ignore duplicate spaces
        String[] tokens = line.split("\\s+");
        Double y = Double.parseDouble(tokens[0]);
        List<IndexValue> x = new ArrayList<>(tokens.length - 1);
        int lastIndex = -1;
        for (int i = 1; i < tokens.length; i++) {
            String[] keyvalue = tokens[i].split(":");
            int index = Integer.parseInt(keyvalue[0]);
            if (index < 0) {
                throw new NumberFormatException(String.format(
                        "Index should not be negative but found %d", index));
            }
            if (lastIndex >= index) {
                throw new NumberFormatException(String.format(
                        "Index should be strongly increasing but found this index is %d but last index is %d",
                        index, lastIndex));
            }
            lastIndex = index;
            IndexValue indexValue = new IndexValue(index, Double.parseDouble(keyvalue[1]));
            x.add(indexValue);
        }
        return new DataPoint(y, x);
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("y", y));
        fields.add(new SignificantField("x", x));
        return fields;
    }

    public static List<DataPoint> listOf(byte[] dataByteArray) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(dataByteArray);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<DataPoint> data = new ArrayList<>();
        while(reader.ready()) {
            String line = reader.readLine().trim();
            if( !line.equals(Constant.EMPTY_STRING)) {
                data.add(DataPoint.parseDataPoint(line));
            }
        }
        return data;
    }
}
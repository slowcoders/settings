package org.slowcoders.io.serialize;

import java.io.IOException;

import org.slowcoders.json.JSONStringer;

public class JSONWriter extends TextWriter {

    private Appendable builder;
    private DataWriter parent;

    private final boolean isMap_;
    private boolean isMap;
    private int itemCnt = 0;
    private boolean hasPendingComma;

    private int indent;

    public JSONWriter(DataWriter parent, String compositeType, boolean isMap, Appendable builder) throws IOException {
        this(parent, compositeType, isMap, builder, 0);
    }

    private JSONWriter(DataWriter parent, String compositeType, boolean isMap, Appendable builder, int indent) throws IOException {
        super(parent != null ? parent.getLoader() : IOAdapter.getLoader(false));
        this.builder = builder;
        this.indent = indent;

        if ((isMap |= (parent == null))) {
            this.builder.append("{");
            newLine();
        } else {
            this.builder.append("[");
        }
        if (compositeType != null) {
            this.builder.append("/*type=" + compositeType + "*/\n");
        }

        this.isMap_ = isMap;
        this.isMap = isMap;
        this.parent = parent;
    }

    private void newLine() throws IOException {
        this.builder.append('\n');
        for (int i = 0; i < indent; i++){
            this.builder.append('\t');
        }
    }

    private void newLineWithClosingMap() throws IOException {
        this.builder.append('\n');
        for (int i = 0; i < indent - 1; i++){
            this.builder.append('\t');
        }
    }

    @Override
    public void writeBoolean(boolean value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeByte(byte value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeChar(char value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeShort(short value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeInt(int value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeLong(long value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeFloat(float value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeDouble(double value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeString(String value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeNumber(Number value) throws Exception {
        appendValue(value);
    }

    @Override
    public void writeNull() throws IOException {
        appendValue(null);
    }

    private void appendAggregatedString(String map) throws IOException {
        if (hasPendingComma) { // JJ 확인 !!
            builder.append(",");
        }
        appendComma();
        builder.append(map);
        itemCnt++;
    }

    private void appendValue(Object value) throws IOException {
        if (this.hasPendingComma){
            builder.append(",");
            if (isMap){
                newLine();
            }
            this.hasPendingComma = false;
        }
        if (!isMap) {
            if (value instanceof String) {
                String quotedString = JSONStringer.getQuotedText((String) value, '\"');
                builder.append(quotedString);
            } else if (value instanceof Character) {
                String quotedString = JSONStringer.getQuotedText(String.valueOf(value), '\'');
                builder.append(quotedString);
            } else {
                builder.append(String.valueOf(value));
            }
            appendComma();
            return;
        }
        if (itemCnt % 2 == 0) {
            builder.append("\"");
            builder.append(String.valueOf(value));
            builder.append("\"");
            builder.append(" ");
            builder.append(":");
            builder.append(" ");
        } else {
            if (value instanceof String) {
                String quotedString = JSONStringer.getQuotedText((String) value, '\"');
                builder.append(quotedString);
            } else if (value instanceof Character) {
                String quotedString = JSONStringer.getQuotedText(String.valueOf(value), '\'');
                builder.append(quotedString.charAt(0));
            } else {
                builder.append(String.valueOf(value));
            }
            appendComma();
        }
        itemCnt++;
    }

    private void appendComma() throws IOException {
        this.hasPendingComma = true;
    }

    public String toString() {
        return builder.toString();
    }

    @Override
    protected boolean isMap() {
        return this.isMap;
    }

    @Override
    public void close() throws IOException {
        this.hasPendingComma = false;
        if (isMap) {
            newLineWithClosingMap();
            builder.append("}");
        } else {
            builder.append("]");
        }
        if (parent != null) {
            ((JSONWriter) parent).appendAggregatedString(builder.toString());
        }
    }

    @Override
    public boolean writeArrayStart(Object values) throws IOException {
        if (values == null) {
            writeNull();
            return false;
        }
        if (hasPendingComma){
            builder.append(",");
            hasPendingComma = false;
        }
        builder.append("[");
        this.isMap = false;
        itemCnt++;
        return true;
    }

    @Override
    public void writeArrayEnd() throws IOException {
        builder.append("]");
        appendComma();
        this.isMap = this.isMap_;
    }

    protected AggregatedStream beginAggregate(String compositeType, boolean isMap) throws Exception {
        return new JSONWriter(this, compositeType, isMap, this.builder.getClass().newInstance(), indent + 1);
    }

    public static void writeObject(Object entity, Appendable writer) throws Exception {
        JSONWriter out = new JSONWriter(null, null, true, writer);
        IOField[] fields = IOEntity.registerSerializableFields(entity.getClass());
        out.writeEntity(entity, fields);
        out.close();
    }
}

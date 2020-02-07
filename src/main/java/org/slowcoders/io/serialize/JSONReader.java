package org.slowcoders.io.serialize;

import org.slowcoders.io.util.CharArrayWriterNTS;
import org.slowcoders.json.JSONScanner;
import org.slowcoders.util.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class JSONReader extends TextReader {

    private JSONScanner sc;
   //private JSONScanner oldSc;

    private CharArrayWriterNTS buffer;

    private boolean wasNull;
    private String aggregatedType;

    public JSONReader(IOAdapterLoader loader, Reader reader) throws Exception {
        this(loader, new JSONScanner(reader), detectIsMap(reader));
    }

	@Override
	public boolean wasNull() throws Exception {
		return this.wasNull;
	}

    protected JSONReader(IOAdapterLoader loader, JSONScanner sc, boolean isMap) throws Exception {
        super(loader, isMap);
        this.sc = sc;
        buffer = new CharArrayWriterNTS();
        sc.setRawInputMode(true);
        int ch = sc.skipWhiteSpace();
        if (sc.trySkipAscii("/*type=", true)) {
            sc.scanUntil('*', buffer);
            sc.skipChar('/');
            this.aggregatedType = buffer.reset();
            ch = sc.skipWhiteSpace();
        }
        sc.setRawInputMode(false);

        switch (ch) {
            case '}': case ']':
                break;
            default:
                sc.pushBack(',');
        }
    }


    private static boolean detectIsMap(Reader reader) throws IOException {
        while (true){
            int ch = reader.read();
            switch (ch) {
                case '{': case -1:
                    return true;
                case '[':
                    return false;
                default:
                    throwSyntaxError("Invalid JSON format.");
            }
        }
    }

    private String readIdentifier() throws IOException {
        sc.skipWhiteSpace();
        String s = sc.readIdentifier();
        return s;
    }

    private boolean trySkipComma() throws IOException {
        int ch = sc.skipWhiteSpace();
        if ((char) ch != ',') {
            return false;
        }
        sc.skipChar();
        return true;
    }


    private String readUntil(int end) throws IOException {
        buffer.flush();
        this.wasNull = false;

        while (true) {
            int ch = sc.read();
            buffer.write((char) ch);
            if (ch == end) {
                break;
            }
        }
        String text = buffer.reset();
        return text;
    }

    @Override
    public boolean readBoolean() throws Exception {
        this.wasNull = false;
        String v = readIdentifier();
        if ("true".equalsIgnoreCase(v)) {
            return true;
        }
        if ("false".equalsIgnoreCase(v)) {
            return false;
        }
        if ("null".equalsIgnoreCase(v)) {
            this.wasNull = true;
            return false;
        }
        throwSyntaxError("");
        return false;
    }

    @Override
    public byte readByte() throws Exception {
        long v = readLong();
        if (v < Byte.MIN_VALUE || v > Byte.MAX_VALUE) {
            throwSyntaxError("");
        }
        return (byte) v;
    }

    @Override
    public short readShort() throws Exception {
        long v = readLong();
        if (v < Short.MIN_VALUE || v > Short.MAX_VALUE) {
            throwSyntaxError("");
        }
        return (short) v;
    }

    @Override
    public char readChar() throws Exception {
        int ch = sc.readPrintable();
        if (ch != '\'') {
            throwSyntaxError("char token ['] not fount ");
        }
        String v = sc.readQuotedText('\'');
        if (v.length() != 1) {
            throwSyntaxError("");
        }
        return v.charAt(0);
    }

    @Override
    public int readInt() throws Exception {
        long v = readLong();
        if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
            throwSyntaxError("");
        }
        return (int) v;
    }

    @Override
    public long readLong() throws Exception {
        this.wasNull = false;
        int ch = sc.readPrintable();
        boolean isNegative = false;
        if (ch == '+') {
            ch = sc.read();
        } else if (ch == '-'){
            ch = sc.read();
            isNegative = true;
        }

        long v = 0;
        while (ch >= '0' && ch <= '9') {
            v = v * 10 + ch - '0';
            ch = sc.read();
        }

        sc.pushBack(ch);
        if (String.valueOf((char)ch).equalsIgnoreCase("n")){
            String s = readIdentifier();
            if (!s.equalsIgnoreCase("null")){
                throwSyntaxError("");
            } else {
                this.wasNull = true;
            }
        }

        if (isNegative){
            v = -v;
        }
        return v;
    }

    @Override
    public float readFloat() throws Exception {
        double v = readDouble();
        if (v < Float.MIN_VALUE || v > Float.MAX_VALUE) {
            throwSyntaxError("");
        }
        return (float) v;
    }

    @Override
    public double readDouble() throws Exception {
        int ch = sc.peekChar();
        long decimal = 0;
        long integer = 0;
        int leadingZero = 1;

        if (ch != '.') {
            integer = readLong();
        }
        if (sc.peekChar() == '.'){
            sc.read();
            while (sc.peekChar() == '0'){
                leadingZero *= 10;
                if (sc.read() != '0'){
                    break;
                }
            }
            decimal = readLong();
        }

        double v = toDecimal(decimal) / leadingZero;

        return integer + v;
    }

    private double toDecimal(long decimal) {
        int divider = 10;
        while (true){
            boolean needMoreDivision = (double)decimal / divider > 1;
            if (!needMoreDivision){
                break;
            }
            divider *= 10;
        }
        return (double)decimal / divider;
    }

    @Override
    public Object readPrimitiveOrStrings() throws Exception {
        int ch = sc.skipWhiteSpace();
        switch (ch) {
            case 'n': case 't': case 'f':  // null | true | false
                boolean v = readBoolean();
                return this.wasNull() ? null : v;

            case '\"': case '\'':
                return readString();

            case '[':
                return this.readStringArray();

            case '{':
                break;

            default:
                return this.readDouble();
        }
        throw throwSyntaxError("Not a primitives");
    }

    public Object readAny() throws Exception {
        int ch = sc.skipWhiteSpace();
        switch (ch) {
            case 'n': case 't': case 'f':  // null | true | false
                boolean v = readBoolean();
                return this.wasNull() ? null : v;

            case '\"': case '\'':
                return readString();

            case '[':
                int len = this.readObjectArrayLength();
                if (len < 0) {
                    return null;
                }
                Object[] values = new Object[len];
                for (int i = 0; i < len; i++) {
                    values[i] = this.readAny();
                    this.skipArrayItemSeparator();
                }
                this.readEndOfArray();
                return values;

            case '{':
                AutoCloseStream in = this.openChunkedStream();
                HashMap map = new HashMap<>();
                while (!in.isClosed()) {
                    String key = in.readKey();
                    Object value = in.readAny();
                    map.put(key, value);
                }
                return map;

            default:
                return this.readDouble();
        }
    }

    @Override
    public Number readNumber() throws Exception {
        throw Debug.notImplemented();
    }

    @Override
    public String readString() throws Exception {
        int ch = sc.readPrintable();
        switch (ch) {
            case '\'': case '\"':
                return sc.readQuotedText((char)ch);

            case 'n':
                if (sc.trySkipAscii("ull", true)) {
                    return null;
                }
                // no break;

            default:
                throw throwSyntaxError("Quotation not found");
        }
    }

    @Override
    protected AutoCloseStream openChunkedStream() throws Exception {
        int ch = sc.readPrintable();
        JSONReader jsonReader;
        switch ((char) ch) {
            case '[': case '{':
                jsonReader = createChunkedStream(this.getLoader(), this.sc, ch == '{');
                break;
            case 'n':
                if (sc.trySkipAscii("ull", true)) {
                    jsonReader = null;
                    break;
                }
            default:
                throw throwSyntaxError("");
        }
        return jsonReader;
    }

    protected JSONReader createChunkedStream(IOAdapterLoader loader, JSONScanner sc, boolean isMap) throws Exception {
        return new JSONReader(loader, sc, isMap);
    }

    private static RuntimeException throwSyntaxError(String s) {
        throw new RuntimeException("Syntax Error : " + s);
    }

    @Override
    protected String getEntityType() {
        return aggregatedType;
    }

    @Override
    public boolean isClosed() {
        int ch = -1;
        try {
            ch = sc.readPrintable();
            switch (ch) {
                case ',':
                    return false;
                case '}':
                case ']':
                    if (((char) ch == '}') != this.isMap()) {
                        break;
                    }
                    return true;
                case -1:
                    // 내용이 전혀 없는 파일이거나, 내용의 일부가 잘려나간 파일이다.
                    // 전자의 경우에만, true 를 반환해야 한지 않을까?
                    return true;
                default:
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw throwSyntaxError("unexpected token: " + ch);
    }

    @Override
    public String readKey() throws Exception {
        String key = readString();
        this.skipKeySeparator();
        return key;
    }

    @Override
    public void skipKeySeparator() throws Exception {
        sc.skipWhiteSpace();
        if (!sc.skipChar(':')) {
            throwSyntaxError("':' not found");
        }
    }

    @Override
    protected int getItemCount() throws IllegalStateException {
        return -1;
    }

    static char[] itemSeperator = new char[]{',', ']'};
    @Override
    protected int readPrimitiveArrayLength() throws Exception {
        int ch = sc.readPrintable();
        if (ch != '[') {
            if (sc.trySkipAscii("ull", true)) {
                return -1;
            }
            throwSyntaxError("");
        }

        int commaCnt = 0;

        sc.mark(4096);
        loop: while (true) {
            ch = sc.readPrintable();
            switch(ch) {
                case ']':
                    break loop;
                case ',':
                    commaCnt++;
                    continue;
                case '\'':
                case '\"':
                    sc.skipChar();
                    sc.scanQuotation((char) ch, null);
                    ch = sc.readPrintable();
                    break;
                default:
                    ch = sc.scanUntil(itemSeperator, null);
            }
            commaCnt++;
            if (ch == ']') {
                break loop;
            }
            if (ch != ',') {
                throwSyntaxError("invalid array");
            }
        }
        sc.reset();

        return commaCnt;
    }

    @Override
    protected void skipArrayItemSeparator() throws IOException {
        // todo : 오류처리 검토
        trySkipComma();
    }

    @Override
    protected int readObjectArrayLength() throws Exception {
        return readPrimitiveArrayLength();
    }

    @Override
    protected void readEndOfArray() throws Exception {
        int ch = this.sc.readPrintable();
        if (ch != ']') {
            throwSyntaxError("");
        }
        //this.sc = this.oldSc;
    }

    public static void readObject(Object entity, Reader reader) throws Exception {
        AutoCloseStream in = new JSONReader(IOAdapter.getLoader(true), reader);
        IOField[] fields = IOEntity.registerSerializableFields(entity.getClass());
        in.readEntity(entity, fields);
    }
}

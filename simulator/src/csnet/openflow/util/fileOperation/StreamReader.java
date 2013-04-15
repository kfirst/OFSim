package csnet.openflow.util.fileOperation;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StreamReader implements DataInput {
    private FileInputStream fis;
    private BufferedInputStream bis;
    private DataInputStream dis;

    public StreamReader(String fileName) throws FileNotFoundException {
        fis = new FileInputStream(fileName);
        bis = new BufferedInputStream(fis);
        dis = new DataInputStream(bis);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return dis.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return dis.readByte();
    }

    @Override
    public char readChar() throws IOException {
        return dis.readChar();
    }

    @Override
    public double readDouble() throws IOException {
        return dis.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return dis.readFloat();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        dis.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        dis.readFully(b, off, len);
    }

    @Override
    public int readInt() throws IOException {
        return dis.readInt();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String readLine() throws IOException {
        return dis.readLine();
    }

    @Override
    public long readLong() throws IOException {
        return dis.readLong();
    }

    @Override
    public short readShort() throws IOException {
        return dis.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return dis.readUTF();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return dis.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return dis.readUnsignedShort();
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return dis.skipBytes(n);
    }

    public void close() throws IOException {
        dis.close();
        bis.close();
        fis.close();
    }
}

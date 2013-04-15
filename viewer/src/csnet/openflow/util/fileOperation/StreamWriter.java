package csnet.openflow.util.fileOperation;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamWriter implements DataOutput {
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private DataOutputStream dos;

    public StreamWriter(String fileName, boolean append)
            throws FileNotFoundException {
        fos = new FileOutputStream(fileName, append);
        bos = new BufferedOutputStream(fos);
        dos = new DataOutputStream(bos);
    }

    @Override
    public void write(int b) throws IOException {
        dos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        dos.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        dos.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        dos.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        dos.writeByte(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        dos.writeBytes(s);
    }

    @Override
    public void writeChar(int v) throws IOException {
        dos.writeChar(v);
    }

    @Override
    public void writeChars(String s) throws IOException {
        dos.writeChars(s);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        dos.writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        dos.writeFloat(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        dos.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        dos.writeLong(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        dos.writeShort(v);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        dos.writeUTF(s);
    }

    public void close() throws IOException {
        dos.close();
        bos.close();
        fos.close();
    }
}

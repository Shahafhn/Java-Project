package project.serverside;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientConnection implements Transmittable, Closeable {

    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;
    private ThreadCrashListener threadCrashListener;

    ClientConnection(Socket socket, ThreadCrashListener threadCrashListener){
        this.threadCrashListener = threadCrashListener;
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            crashConnection();
        }
    }

    @Override
    public void sendBytes(int action){
        try {
            outputStream.write(action);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(byte[] buffer){
        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(String text){
        byte[] buffer = text.getBytes();
        try {
            outputStream.write(text.length());
            outputStream.write(buffer);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendInt(int num){
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(num);
        sendBytes(bytes);
    }

    @Override
    public void sendLong(long num) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(num);
        sendBytes(bytes);
    }

    @Override
    public int getNextAction(){
        int action = 0;
        try {
            action = inputStream.read();
            if (action == -1)
                crashConnection();
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
        return action;
    }

    @Override
    public byte[] getNextBytes(int length) {
        byte[] temp = new byte[length];
        int read;
        try {
            read = inputStream.read(temp);
            if (read != length) {
                crashConnection();
                return null;
            }
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public int getNextInt() {
        byte[] bytes = getNextBytes(4);
        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public long getNextLong() {
        byte[] bytes = getNextBytes(8);
        return ByteBuffer.wrap(bytes).getLong();
    }

    @Override
    public String getNextString(int min,int max){
        int read;
        byte[] buffer;
        String temp = null;
        try {
            read = inputStream.read();
            if (read < min || read > max){
                crashConnection();
                return null;
            }
            buffer = new byte[read];
            read = inputStream.read(buffer);
            if (read != buffer.length){
                crashConnection();
                return null;
            }
            temp = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String getNextString(){
        return getNextString(0,255);
    }

    @Override
    public void crashConnection(){
        threadCrashListener.crashThread();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Closing Thread.");
        if (socket != null)
            socket.close();
        if (inputStream != null)
            inputStream.close();
        if (outputStream != null)
            outputStream.close();
    }
}
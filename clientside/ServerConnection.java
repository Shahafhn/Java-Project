package project.clientside;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerConnection implements Transmittable, Closeable {

    private ThreadCrashListener threadCrashListener;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Socket socket;

    ServerConnection(ThreadCrashListener threadCrashListener) {
        //saves on-click listener in case of a crash
        this.threadCrashListener = threadCrashListener;
    }

    void setConnection(Socket socket) {
        //sets the server socket connection
        this.socket = socket;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(int action) {
        try {
            outputStream.write(action);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendBytes(String text) {
        byte[] bytes = text.getBytes();
        try {
            outputStream.write(bytes.length);
            outputStream.write(bytes);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
    }

    @Override
    public void sendInt(int num) {
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
    public int getNextAction() {
        int action = 0;
        try {
            action = inputStream.read();
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
            if (read != length)
                crashConnection();
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
    public String getNextString(int min, int max) {
        int read;
        byte[] buffer;
        String temp = null;
        try {
            read = inputStream.read();
            buffer = new byte[read];
            read = inputStream.read(buffer);
            if (read != buffer.length || buffer.length < min || buffer.length > max) {
                crashConnection();
                return null;
            }
            temp = new String(buffer);
        } catch (IOException e) {
            crashConnection();
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public String getNextString() {
        return getNextString(0, 255);
    }

    @Override
    public void crashConnection() {
        threadCrashListener.crashThread();
    }

    void saveProgress(String path) {        //saves the user progress on his request
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(path, 0, 50);
        File dir = new File(stringBuilder.toString());
        if (!dir.isDirectory())     //checks if the project has a save-file directory on this current computer.
            dir.mkdirs();           //if no, creates a directory to save the file.
        try {
            OutputStream os = new FileOutputStream(path);   //creates the save file
            byte[] buffer;
            buffer = getNextBytes(8);
            os.write(buffer);
            for (int j = 0; j < 2; j++) {
                buffer = getNextBytes(8);
                os.write(buffer);
                buffer = getNextBytes(4);
                os.write(buffer);
            }
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    buffer = getNextBytes(8);
                    os.write(buffer);
                }
                for (int k = 0; k < 2; k++) {
                    buffer = getNextBytes(4);
                    os.write(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException{     //closes connectiong
        if (inputStream != null)
            inputStream.close();
        if (outputStream != null)
            outputStream.close();
        if (socket != null)
            socket.close();
    }
}

package project.clientside;

interface Transmittable {
    void sendBytes(int action);
    void sendBytes(byte[] buffer);
    void sendBytes(String text);
    void sendInt(int num);
    void sendLong(long num);
    int getNextAction();
    byte[] getNextBytes(int length);
    int getNextInt();
    long getNextLong();
    String getNextString(int min,int max);
    String getNextString();
    void crashConnection();
}

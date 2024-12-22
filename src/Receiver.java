import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver implements Runnable{
    //接收线程，用于客户端接收服务器数据的作用
    Socket socket;

    public Receiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //从服务器读取数据
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

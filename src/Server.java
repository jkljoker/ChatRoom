import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<String> users = new ArrayList<>();
    static ArrayList<BufferedWriter> sockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        infoUsers();
        int port = 12345;
        ServerSocket ss = new ServerSocket(port);
        System.out.println("服务器已启动，正在监听端口：" + port);
        while (true) {
            //和客户端获得连接
            Socket s = ss.accept();
            System.out.println("已连接客户端：" + s.getInetAddress().getHostName());
            new Thread(new MessageHandler(s, users, sockets)).start();
        }
    }

    //从本地文件中读取所有用户信息
    private static void infoUsers() throws IOException {
        users.clear();
        BufferedReader br = new BufferedReader(new FileReader("userinfo.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            users.add(line);
        }
    }
}

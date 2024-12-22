import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static BufferedWriter bw;
    private static BufferedReader br;
    private static Socket s;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        //连接到服务器
        String host = "127.0.0.1";
        int port = 12345;
        s = new Socket(host, port);
        System.out.println("已连接到服务器");

        //获取流
        bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //接下来写登录注册功能
        while (true) {
            System.out.println("请选择功能: 1登录 2注册");
            int input = Integer.parseInt(sc.nextLine());
            String username;
            String password;
            String receive;

            if (input == 1) {
                bw.write("1");
                bw.newLine();
                bw.flush();
                //输入用户名和密码，并传给服务器端，让服务器进行判断+
                System.out.println("请输入用户名:");
                username = sc.nextLine();
                System.out.println("请输入密码:");
                password = sc.nextLine();
                bw.write(username);
                bw.newLine();
                bw.write(password);
                bw.newLine();
                bw.flush();

                receive = br.readLine();
                if ("登录成功".equals(receive)) {
                    System.out.println(receive);
                    //启动接收线程
                    new Thread(new Receiver(s)).start();
                    chat();
                    break;
                } else {
                    System.out.println(receive);
                }
            } else {
                bw.write("2");
                bw.newLine();
                bw.flush();
                //注册
                System.out.println("请输入要创建的用户名:");
                username = sc.nextLine();
                System.out.println("请输入密码:");
                password = sc.nextLine();

                //传给服务器，让服务器判断是否合法
                bw.write(username);
                bw.newLine();
                bw.write(password);
                bw.newLine();
                bw.flush();

                //接收服务器的传回信号,就算注册成功也不能退出循环
                receive = br.readLine();
                System.out.println(receive);
            }
        }
    }

    //聊天室功能实现
    private static void chat() throws IOException {
        while (true) {
            System.out.println("请输入:");
            String input = sc.nextLine();
            if ("886".equals(input)) {
                System.out.println("客户端已断开连接");
                break;
            } else {
                bw.write(input);
                bw.newLine();
                bw.flush();
            }
        }
        bw.close();
        br.close();
        s.close();
    }
}
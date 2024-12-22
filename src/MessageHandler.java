import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MessageHandler implements Runnable {
    static ArrayList<String> users;
    static ArrayList<BufferedWriter> sockets;

    String username;
    String password;
    String result;

    // 正则表达式
    String usernameRegex = "^[a-zA-Z]{6,18}$";
    String passwordRegex = "^[a-zA-Z]\\d{2,7}$";

    // 用户操作选择（登录或注册）
    String select;
    Socket s;

    BufferedReader br;
    BufferedWriter bw;

    public MessageHandler(Socket socket, ArrayList<String> users, ArrayList<BufferedWriter> sockets) throws IOException {
        this.s = socket;
        this.users = users;
        this.sockets = sockets;
        this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                select = br.readLine();
                if ("1".equals(select)) {  // 登录
                    System.out.println("客户端正在登录");
                    username = br.readLine();
                    password = br.readLine();
                    searchUser();
                    bw.write(result);
                    bw.newLine();
                    bw.flush();
                    if ("登录成功".equals(result)) {
                        System.out.println("登录成功");
                        break;
                    } else {
                        System.out.println("登录失败");
                    }
                } else {  // 注册
                    System.out.println("客户端正在注册");
                    username = br.readLine();
                    password = br.readLine();
                    register();
                    System.out.println(result);
                    bw.write(result);
                    bw.newLine();
                    bw.flush();
                    System.out.println(username + "账号创建成功");
                }
            }

            // 只有登录成功后才执行
            sockets.add(bw);
            String message;
            while ((message = br.readLine()) != null) {
                System.out.println(username + ": " + message);
                for (BufferedWriter socketWriter : sockets) {
                    socketWriter.write(username + ": " + message);
                    socketWriter.newLine();
                    socketWriter.flush();
                }
            }
            s.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (br != null) br.close();
                if (bw != null) bw.close();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    private void register() throws IOException {
        if (!username.matches(usernameRegex)) {
            result = "用户名不合法";
            return;
        }
        if (!password.matches(passwordRegex)) {
            result = "密码不合法";
            return;
        }
        synchronized (users) {
            for (String user : users) {
                String[] split = user.split("=");
                if (split[0].equals(username)) {
                    result = "用户名已存在";
                    return;
                }
            }
            // 用户可创建，写入文件
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("userinfo.txt", true))) {
                fileWriter.write(username + "=" + password);
                fileWriter.newLine();
            }
            result = "创建成功";
        }
        infoUsers();
    }

    private void searchUser() {
        synchronized (users) {
            for (String user : users) {
                String[] split = user.split("=");
                if (username.equals(split[0]) && password.equals(split[1])) {
                    result = "登录成功";
                    return;
                } else if (username.equals(split[0])) {
                    result = "密码错误";
                    return;
                }
            }
        }
        result = "用户不存在";
    }

    //从本地文件中读取所有用户信息
    private void infoUsers() throws IOException {
        users.clear();
        BufferedReader br = new BufferedReader(new FileReader("userinfo.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            users.add(line);
        }
    }
}

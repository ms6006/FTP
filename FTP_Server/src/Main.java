//Server FTP

import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class Main {
    public static void main(String args[]) {
        try {
            new Server(3128).start();
        } catch (Exception e) {
            System.out.print("Error with port!");
        }
    }
}

class Server {
    int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        ServerSocket ss = new ServerSocket(port);
        while (true) {
            new Client(ss.accept()).start();
        }
    }
}

class Client extends Thread {
    Socket cs;
    DataInputStream dis;
    DataOutputStream dos;

    public Client(Socket cs) {
        this.cs = cs;
    }

    public void writeMessage(String s) throws Exception {
        dos.writeUTF(s);
    }

    public void send() {
        try {
            long fS = dis.readLong();
            String fN = dis.readUTF();
            System.out.println(fS);
            System.out.println(fN);
            byte[] buffer = new byte[64 * 1024];
            FileOutputStream outF = new FileOutputStream("src/files/" + fN);
            int count, total = 0;
            while ((count = dis.read(buffer)) != -1) {
                total += count;
                outF.write(buffer, 0, count);
                if (total == fS) {
                    break;
                }
            }
            outF.flush();
            outF.close();
        } catch (Exception e) {
        }
    }

    public void get(String name) {
        File myFolder = new File("src/files/");
        File[] files = myFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(name)) {
//dos.writeUTF("send");
                File f = new File("src/files/" + name);
                try {
                    dos.writeLong(f.length());
                    dos.writeUTF(f.getName());
                    FileInputStream in = new FileInputStream(f);
                    byte[] buffer = new byte[64 * 1024];
                    int count;

                    while ((count = in.read(buffer)) != -1) {
                        dos.write(buffer, 0, count);
                    }
                    dos.flush();
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void GetList() throws Exception {
        File myFolder = new File("src/files/");
        File[] files = myFolder.listFiles();
        int tl = files.length;
        String Tl = String.valueOf(tl);
        dos.writeUTF(Tl);
        for (int i = 0; i < files.length; i++)
            dos.writeUTF(files[i].getName());
    }

    public void selector(String s) throws Exception {
        String[] cmd = s.split(" +", 2);

        switch (s) {

            case "send":
                send();
                break;

            case "login":
                boolean f = false;
                String l = dis.readUTF();
                String p = dis.readUTF();
                Scanner sb = new Scanner(new File("src/UserR.txt"));
                while (sb.hasNext()) {
                    String[] user = sb.nextLine().split(":+", 2);
                    if (l.equals(user[0]) && p.equals(user[1])) {
                        f = true;
                        break;
                    }
                }
                if (f) {
                    dos.writeUTF("log");
                } else dos.writeUTF("nolog");
                break;

            case "get":
                String name = dis.readUTF();
                get(name);
                break;

            case "GetList":
                GetList();
                break;
        }

    }

    public void run() {
        try {
            String s = "";
            System.out.println("Connect from " + cs.getInetAddress());
            dis = new DataInputStream(cs.getInputStream());
            dos = new DataOutputStream(cs.getOutputStream());
            do {
                try {
                    s = dis.readUTF();
                } catch (Exception e) {
                    s = "exit";
                    dos.close();
                    dis.close();
                    cs.close();
                    System.out.println("Disconnect from " + cs.getInetAddress());
                }

                selector(s);
            } while (!s.equals("exit"));
            dos.close();
            dis.close();
            cs.close();
        } catch (Exception e) {
        }
    }
}

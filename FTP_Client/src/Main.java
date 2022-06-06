//Client FTP

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.lang.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class Main {
    public static void main(String[] args) {
        try {
            Client cl = new Client("localhost", 3128);
            cl.start();
        } catch (Exception e) {
            System.out.print("ERROR:\n> Can't connect! Server is offline! Try again later!!\n");
        }
    }
}

class Client {
    String b = "";
    DataInputStream dis;
    DataOutputStream dos;
    Socket cs;
    String ip;
    JLWindow jlw;
    JWindow jw;
    int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() throws Exception {
        cs = new Socket(ip, port);
        dis = new DataInputStream(cs.getInputStream());
        dos = new DataOutputStream(cs.getOutputStream());
        jlw = new JLWindow("Login", this);
        jw = new JWindow("Client", this, cs);
    }

    public void exit() throws Exception {
        dos.close();
        dis.close();
        cs.close();
    }

    public void send(String name) throws Exception {
        File myFolder = new File("src/files/");
        File[] files = myFolder.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(name)) {
                dos.writeUTF("send");
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

    //    Get Server File
    public void get(String name) throws Exception {
        dos.writeUTF("get");
        dos.writeUTF(name);

        try {
            long fS = dis.readLong();
            String fN = dis.readUTF();
            System.out.println(fS);
            System.out.println(fN);
            byte[] buffer = new byte[64 * 1024];
            FileOutputStream outF = new FileOutputStream("src/GetFiles/" + fN);
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

//

    public void login(String l, String p) throws Exception {
        dos.writeUTF("login");
        dos.writeUTF(l);
        dos.writeUTF(p);
        String ans = dis.readUTF();

        if (ans.equals("log")) {
            jlw.setVisible(false);
            jw.setVisible(true);
        } else if (ans.equals("nolog")) {
            JOptionPane.showMessageDialog(null, "Wrong login or password! Try Again");
        }
    }
}

class JWindow extends JFrame {
    Client cl;
    Socket cs;

    public JWindow(String title, Client cl, Socket cs) {
        super(title);
        setSize(500, 250);
        setLocation(0, 0);
        setLayout(null);
        this.cl = cl;
        this.cs = cs;
        Container c = getContentPane();

        c.setBackground(new Color(127, 127, 127));
        c.add(new JB("Send file...", cl, cs));
        c.add(new JB1("Get", cl, cs));

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  if (JOptionPane.showConfirmDialog(null, "Вы уверены?") == JOptionPane.OK_OPTION)
                                      try {
                                          //cl.exit();
                                      } catch (Exception e) {
                                      }
                                  ;
                                  System.exit(0);
                              }
                          }
        );

        setVisible(false);
    }
}

class JB extends JButton implements ActionListener {
    Client cl;
    Socket cs;

    public JB(String title, Client cl, Socket cs) {
        super(title);
        setSize(150, 30);
        setLocation(0, 0);
        addActionListener(this);
        this.cl = cl;
        this.cs = cs;
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            int
                    t = 1;
            JCWindow jlw = new JCWindow("Choose file!", cl, t, cs);
            //cl.send();
        } catch (Exception e) {
        }
    }

}

class JB1 extends JButton implements ActionListener {
    Client cl;
    Socket cs;

    public JB1(String title, Client cl, Socket cs) {
        super(title);
        setSize(150, 30);
        setLocation(0, 100);
        addActionListener(this);
        this.cl = cl;
        this.cs = cs;
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            int t = 2;
            JCWindow jlw = new JCWindow("Choose file!", cl, t, cs);
            //cl.send();
        } catch (Exception e) {
        }
    }

}

class JLWindow extends JFrame {
    Client cl;

    public JLWindow(String title, Client cl) {
        super(title);
        setSize(400, 300);
        setLocation(0, 0);
        setLayout(null);
        this.cl = cl;
        Container c = getContentPane();
        c.setBackground(new Color(127, 127, 127));
        JText login = new JText();
        JText pass = new JText();
        pass.setLocation(125, 125);
        c.add(new JLB("Login", cl, login, pass));

        c.add(login);
        c.add(pass);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  if (JOptionPane.showConfirmDialog(null, "Вы уверены?") == JOptionPane.OK_OPTION)
                                      try {
                                          //cl.exit();
                                      } catch (Exception e) {
                                      }
                                  ;
                                  System.exit(0);
                              }
                          }
        );
        setVisible(true);
    }

    class JLB extends JButton implements ActionListener {
        Client cl;
        JText login;
        JText pass;
        DataInputStream dis;
        DataOutputStream dos;

        public JLB(String title, Client cl, JText login, JText pass) {
            super(title);
            setSize(150, 30);
            setLocation(125, 160);
            addActionListener(this);
            this.cl = cl;
            this.login = login;
            this.pass = pass;
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                String l = login.getText();
                String p = pass.getText();
                cl.login(l, p);


            } catch (Exception e) {
            }
        }

    }

    class JText extends JTextField {
        public JText() {
            super();
            setLocation(125, 90);
            setSize(150, 30);
            setVisible(true);
        }

    }
}

class JCWindow extends JFrame {
    Client cl;
    JList js;
    String ts;
    Socket cs;
    int t;

    //DataInputStream dis = new DataInputStream(cs.getInputStream());
    public JCWindow(String title, Client cl, int t, Socket cs) {
        super(title);
        setSize(400, 300);
        setLocation(100, 100);
        setLayout(null);
        this.cl = cl;
        this.t = t;
        this.cs = cs;

        Container c = getContentPane();
        c.setBackground(new Color(127, 127, 127));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  //if(JOptionPane.showConfirmDialog(null,"Вы уверены?") == JOptionPane.OK_OPTION)
                                  try {
                                      //cl.exit();
                                  } catch (Exception e) {
                                  }
                                  ;
                                  setVisible(false);
                              }
                          }
        );

        try {
            DataInputStream dis = new DataInputStream(cs.getInputStream());
            DataOutputStream dos = new DataOutputStream(cs.getOutputStream());

            if (t == 1) {
                File myFolder = new File("src/files/");
                File[] files = myFolder.listFiles();
                String[] data = new String[files.length];
                for (int i = 0; i < files.length; i++)
                    data[i] = files[i].getName();
                js = new JList(data);
            } else if (t == 2) {
                dos.writeUTF("GetList");
                int Tl = Integer.parseInt(dis.readUTF());
                String[] data = new String[Tl];
                for (int i = 0; i < Tl; i++)
                    data[i] = dis.readUTF();
                js = new JList(data);
            }
        } catch (Exception e) {
        }

        if (t == 1) {
            c.add(new JBS("Send", cl, t));
        } else {
            c.add(new JBS("Get", cl, t));
        }


        c.setLayout(null);
//js = new JList(data);
        js.setSize(400, 200);
        js.setLocation(0, 0);
        getContentPane().add(js);
        js.setVisible(true);
        js.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ts = js.getSelectedValue().toString();
                System.out.println(ts);
            }
        });
        setVisible(true);
    }


    class JBS extends JButton implements ActionListener {
        Client cl;
        int t;

        public JBS(String title, Client cl, int t) {
            super(title);
            setSize(150, 30);
            setLocation(0, 250);
            addActionListener(this);
            this.cl = cl;
            this.t = t;
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                if (t == 1) {
                    cl.send(ts);
                } else if (t == 2) {
                    cl.get(ts);
                }
            } catch (Exception e) {
            }
        }

    }


}

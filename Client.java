import java.net.*;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;
import java.awt.Color;


public class Client extends JFrame {

    Socket socket;

    // variables
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Arial", Font.ITALIC, 20);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("192.168.2.103", 7776);
            System.out.println("Connectin Done...");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
            // startWriting();

        } catch (Exception e) {
            // Handle Exception
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                // System.out.println("Key Realeased.." + e.getKeyCode());
                if (e.getKeyCode() == 10) {
                    // System.out.println("You have pressed ENTER key..");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me :" + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }

            }

        });
    }

    private void createGUI() {
        // GUI code
        this.setTitle("Client Messanger[END]");
        this.setSize(750, 650);// fixed the chating screen frame
        this.setLocationRelativeTo(null);// pointer to centre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// tab to cross of jframe just exit the program

        // coding for components
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("chat.jpg"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createDashedBorder(Color.BLUE, 2.0f, 10.0f, 1.0f, true));

        messageArea.setEditable(false);

        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // set the layout of frame
        this.setLayout(new BorderLayout());

        // adding the components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);// for window visible

    }

    // starting reading [Method]
    public void startReading() {
        // Thread read the data
        Runnable r1 = () -> {
            System.out.println("Reader Started...");

            try {
                while (true) {

                    String msg = br.readLine();
                    if (msg.equals("quite")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    // System.out.println("Server: " + msg);
                    messageArea.append("Server: " + msg + "\n");

                }
            } catch (Exception e) {
                System.out.println("Connection Closed.");
            }

        };
        new Thread(r1).start();

    }

    // starting writing [Method]
    public void startWriting() {
        // Thread received data and send the data to client
        Runnable r2 = () -> {
            System.out.println("Writer started..");

            try {
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("quit")) {
                        socket.close();
                        break;
                    }

                }
                // System.out.println("Connection Closed.");

            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client...");
        new Client();
    }

}

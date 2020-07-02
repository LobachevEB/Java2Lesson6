import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final int PORT = 51111;
    private final String IP_ADDRESS = "localhost";

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Scanner kbdIn = null;
    private Thread tNetListener = null;
    private Thread tKbdListener = null;

    public void Run() {
        tNetListener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(IP_ADDRESS, PORT);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());

                    while (!Thread.currentThread().isInterrupted()) {
                        String str = in.readUTF();
                        System.out.printf("Server say: %s\n", str);

                        if (str.equals("/end")) {
                            tKbdListener.interrupt();
                            break;
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        tNetListener.start();

        tKbdListener = new Thread(new Runnable() {
            @Override
            public void run() {
                kbdIn = new Scanner(System.in);
                while (!Thread.currentThread().isInterrupted()){
                    String str = "";
                    try {
                        if (System.in.available() > 0) {
                            str = kbdIn.nextLine();
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    try {
                        if(out != null && !str.equals("")){
                            out.writeUTF(str);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if(str.equals("/end")){
                        tNetListener.interrupt();
                        break;
                    }
                }
                try {
                    if(out != null){
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if(in != null){
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        tKbdListener.start();
        try {
            tKbdListener.join();
            tNetListener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

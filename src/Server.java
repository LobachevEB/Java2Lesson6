import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    final int PORT = 51111;
    private ServerSocket myServer = null;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private Scanner kbdIn = null;
    Thread tNetListener = null;
    Thread tKbdListener = null;

    public void Run(){
        try {
            myServer = new ServerSocket(PORT);
            System.out.println("Server started");
            tNetListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = myServer.accept();
                        System.out.println("Client connection accepted. Print '/end' to finish this chat.");
                        in = new DataInputStream(socket.getInputStream());
                        out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("Hello from Server. Print '/end' to finish this chat.");
                        while (!Thread.currentThread().isInterrupted()){
                            try {
                                String str = in.readUTF();
                                System.out.printf("Client say: %s\n", str);
                                if (str.equals("/end")) {
                                    tKbdListener.interrupt();
                                    out.writeUTF(str);
                                    break;
                                }
                            }
                            catch (IOException e){
                                break;
                            }
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                        out.close();
                    }catch (IOException e){
                        System.out.println(e.getMessage());
                    }

                }
            });
            tKbdListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    kbdIn = new Scanner(System.in);
                    while (!Thread.currentThread().isInterrupted()){
                        try {
                            String str = "";
                            if (System.in.available() > 0) {
                                str = kbdIn.nextLine();
                                Thread.sleep(100);
                            }
                            if(out != null && !str.equals("")){
                                out.writeUTF(str);
                            }
                            if(str.equals("/end")){
                                tNetListener.interrupt();
                                break;
                            }
                        } catch (IOException|InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }


                }
            });

            tNetListener.start();
            tKbdListener.start();
            try {
                tKbdListener.join();
                tNetListener.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

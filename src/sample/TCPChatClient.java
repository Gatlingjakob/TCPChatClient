//Example 25
package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TCPChatClient extends Application implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;

    private static boolean closed = false;

    private static String username = "";

    private static ArrayList<String> scannedName = new ArrayList<String>();



    String JOIN = "JOIN {" + username + "}, {" +
            clientSocket.getInetAddress() + "}:{" + clientSocket.getPort()+ "}";
    byte[] JOINBytes = JOIN.getBytes();

    String DATA = "DATA {" + "[" + username + "]" + "}: {" + inputLine.toString() + "}";
    byte[] DATABytes = DATA.getBytes();

    String ALIVE = "ALIVE[" + username + "]" + "is alive";
    byte[] ALVEBytes = ALIVE.getBytes();

    String QUIT = "QUIT" + "[" + username + "]";
    byte[] QUITBytes = QUIT.getBytes();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {

        // The default port.
        int portNumber = 2222;
        // The default host.
        String host = "localhost";

    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
        try {

            clientSocket = new Socket(host, portNumber);

            inputLine = new BufferedReader(new InputStreamReader(System.in));



            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

            //os.write(JOINBytes);
            //os.append(JOINBytes);



        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
        }

    /*
     * If everything has been initialized then we want to write some data to the
     * socket we have opened a connection to on the port portNumber.
     */
        if (clientSocket != null && os != null && is != null) {
            try {

        /* Create a thread to read from the server. */
                new Thread(new TCPChatClient()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
    /*
     * Keep on reading from the socket till we receive "Bye" from the
     * server. Once we received that then we want to break.
     */
        String responseLine = "";



        try {

            while ((responseLine = is.readLine()) != null) {



                if(responseLine.startsWith("Hello ")){
                    //Iterér igennem response line for at få brugernavn til fremtidig brug i de andre PRAHTEKAHLS
                    os.write(JOINBytes);
                    //username = inputLine.toString();
                    //username = responseLine.toString();

                }



                System.out.println(responseLine);
                if (responseLine.contains("Server says Bye")) { //Det er ikke sikkert at denne responseline kommer
                    //på det rigtige tidspunkt
                    os.write(QUITBytes);
                    break;
                }

            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }

    }
}
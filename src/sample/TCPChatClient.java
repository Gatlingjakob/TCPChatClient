//Example 25
package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class TCPChatClient extends Application {

    // The client socket
    private static Socket clientSocket = null;

    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;

    private static boolean closed = false;

    private static String username = "";

    private String tempUsername = "";

    private static ArrayList<String> scannedName = new ArrayList<String>();


    private static Boolean unknownHost = false;
    Scene scene01, scene02;

/*

    String JOIN = "JOIN {" + username + "}, {" +
            clientSocket.getInetAddress() + "}:{" + clientSocket.getPort()+ "}";
    byte[] JOINBytes = JOIN.getBytes();

    String DATA = "DATA {" + "[" + username + "]" + "}: {" + inputLine.toString() + "}";
    byte[] DATABytes = DATA.getBytes();

    String ALIVE = "ALIVE[" + username + "]" + "is alive";
    byte[] ALVEBytes = ALIVE.getBytes();

    String QUIT = "QUIT" + "[" + username + "]";
    byte[] QUITBytes = QUIT.getBytes(); */

    // JavaFX


    @Override
    public void start(Stage primaryStage) throws Exception{

        // --- Log in screen  ---
        Label label01 = new Label("Select a username to join the chat");
        TextField usernameInput = new TextField();
        usernameInput.setMinWidth(100);
        usernameInput.setMaxWidth(200);

        Text errorMessage = new Text();
        Text connectError = new Text();


        Task<Void> taskJoinChat = new Task<Void>() {
            @Override protected Void call() throws Exception {
                String responseLine = "";

                try {

                    while ((responseLine = is.readLine()) != null) {



                        if(responseLine.startsWith("Hello ")){
                            //  os.write(JOINBytes);
                            //username = inputLine.toString();
                            //username = responseLine.toString();

                        }



                        System.out.println(responseLine);
                        if (responseLine.contains("Server says Bye")) { //Det er ikke sikkert at denne responseline kommer
                            //på det rigtige tidspunkt
                            //    os.write(QUITBytes);
                            break;
                        }

                    }
                    closed = true;
                } catch (IOException e) {
                    System.err.println("IOException:  " + e);
                }


                return null;
            }
        };



        // Connect to server
        Platform.runLater(()-> {

            int portNumber = 2222;
            String host = "localhost";


           // Open a socket on a given host and port. Open input and output streams.
            try {

                clientSocket = new Socket(host, portNumber);
                inputLine = new BufferedReader(new InputStreamReader(System.in));


                os = new PrintStream(clientSocket.getOutputStream());
                is = new DataInputStream(clientSocket.getInputStream());



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
                  // Create a thread to read from the server.
                  Thread th = new Thread(taskJoinChat);
                  th.start();
                 //    while (!closed) {
                  //      os.println(inputLine.readLine().trim());
                 //   }

                  //  Close the output stream, close the input stream, close the socket.
                  os.close();
                  is.close();
                  clientSocket.close();
                } catch (IOException e) {
                    System.err.println("IOException:  " + e);
                }
            }

        });


        // Join chat button
        Button btn = new Button("Create user");

        /*
        *  isEmpty returns a boolean. First parameter is the text that the user inputs (String)
        *  and second parameter is a boolean variable that defines if there is a connection or not
        *
        *  joinChat returns a String (for now - change to boolean!!!). First parameter is the text object
        *  write text in the GUI(Text). Second parameter is the input that the user types(String)
        * */
        btn.setOnAction(event -> {
            System.out.println("Button clicked");

            // If the input field is not empty and the application can find the host
           if (!isEmpty(usernameInput.getText()) && !unknownHost) {
               tempUsername = usernameInput.getText();
               usernameInput.clear();
               // JOIN message --- protocol
               String JOIN = "JOIN {" + tempUsername + "}, {" +
                       clientSocket.getInetAddress() + "}:{" + clientSocket.getPort()+ "}";
               byte[] JOINBytes = JOIN.getBytes(); // get byte array from string
               String line = new String(JOINBytes); // convert bytearray to string
               System.out.println(line); // print string


               try {
                   os.write(JOINBytes);
               } catch (IOException e) {
                   e.printStackTrace();
               }
               os.flush();




            /*     // If username is not already taken
                if (!(joinChat(errorMessage, usernameInput).equals("?NO***"))) {
                    username = usernameInput.getText();
                    usernameInput.clear();
                    System.out.println("If message true");
                    System.out.println("Username: " + username);
                    // Go to next page
                    primaryStage.setScene(scene02);

                }
                else { // DIS NOT FUCKING WORKING!!!
                    System.out.println("If statement returned false");
                    errorMessage.setText("Username is taken");
                    usernameInput.clear();
                    errorMessage.setFill(Color.FIREBRICK);
                    errorMessage.setText("Unknown host");
                } */
            }
        });



        // Create new VBox and add all of the buttons and text from above
        VBox layout01 = new VBox(20);
        layout01.getChildren().addAll(label01, usernameInput, btn, errorMessage, connectError);
        layout01.setPadding(new Insets(20, 10, 50, 10)); // Top, right, bottom, left
        layout01.setAlignment(Pos.CENTER);

        BorderPane borderpane01 = new BorderPane();
        borderpane01.setCenter(layout01);
        scene01 = new Scene(borderpane01, 900, 600);


        // --- Chat window ---
        // The bottom text field user types in
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: #00be00");
        TextField textfield = new TextField();
        textfield.setAlignment(Pos.BOTTOM_LEFT);
        paneForTextField.setCenter(textfield);

        // Buttons
        Button btnsend = new Button("Send");
        btnsend.setMinHeight(40);

        Button btnquit = new Button("Leave chat");
        btnquit.setMinHeight(40);


        // Splitpane for text input field and buttons
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(paneForTextField, btnsend, btnquit);
        splitPane.setStyle("-fx-box-border: transparent;");


        // Display conversation
        BorderPane mainPane = new BorderPane();
        TextArea textarea = new TextArea();
        textarea.setMinWidth(900);
        textarea.setMinHeight(600);
        textarea.setEditable(false);
        textarea.setStyle("-fx-text-fill: #1e1e94;");

        mainPane.setCenter(new ScrollPane(textarea));
        mainPane.setBottom(splitPane);


        scene02 = new Scene(mainPane, 900, 600);
        primaryStage.setTitle("Client(TCP)_test01");
        primaryStage.setScene(scene01);
        primaryStage.show();


        // When the user types something
        btnsend.setOnAction(e ->{
            // Check if the user actually typed something
            if (!isEmpty(textfield.getText())) {
                typeMessage(textfield.getText());
                textarea.appendText(username + ": " + textfield.getText() + "\n\n");
                textfield.clear();
            }

        });

    }



    public static void typeMessage(String message) {
        // "Load" outgoing packet by passing it the newly entered message

        }

    // Check if user typed something in the field before submitting
    public static boolean isEmpty(String string) {
        return string.length() == 0;
    }

    // Check if username is taken
    public static boolean isTaken(String string) {
        return false;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
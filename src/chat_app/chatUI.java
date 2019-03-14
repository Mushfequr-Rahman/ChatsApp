package chat_app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import chat_app.server.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class chatUI extends Application
{

    private ArrayList<Thread> threads;



    public static void main(String[] args) {
        launch(args);
    }



    private Scene LoginScene(Stage primaryStage) {

        double WIDTH = 800;
        double HEIGHT = 500;
        // Instantiate a new Grid Pane
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane,WIDTH,HEIGHT);

        // Position the pane at the center of the screen, both vertically and horizontally
        gridPane.setAlignment(Pos.CENTER);

        // Set a padding of 20px on each side
        gridPane.setPadding(new Insets(40, 40, 40, 40));

        // Set the horizontal gap between columns
        gridPane.setHgap(10);

        // Set the vertical gap between rows
        gridPane.setVgap(10);

        // Add Column Constraints

        // columnOneConstraints will be applied to all the nodes placed in column one.
        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        // columnTwoConstraints will be applied to all the nodes placed in column two.
        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);
        addUIControls(gridPane,primaryStage);

        return scene;
    }

    private void addUIControls(GridPane gridPane,Stage primaryStage) {
        // Add Header
        Label headerLabel = new Label("Chatsapp");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gridPane.add(headerLabel, 0,0,2,1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));

        // Add Name Label
        Label nameLabel = new Label("Username : ");
        gridPane.add(nameLabel, 0,1);

        // Add Name Text Field
        TextField nameField = new TextField();
        nameField.setPrefHeight(40);
        gridPane.add(nameField, 1,1);

        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 3);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(40);
        gridPane.add(passwordField, 1, 3);

        // Add Submit Button
        Button loginButton = new Button("Login");
        loginButton.setId("loginButton");
        loginButton.setPrefHeight(40);
        loginButton.setDefaultButton(true);
        loginButton.setPrefWidth(100);
        gridPane.add(loginButton, 0, 4, 2, 1);
        GridPane.setHalignment(loginButton, HPos.CENTER);
        GridPane.setMargin(loginButton, new Insets(20, 0,20,0));

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(nameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", "Please enter your username");
                    return;
                }
                if(passwordField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", "Please enter your password");
                    return;
                }
                //check if username/password matches (copy pasta same code)
                File f = new File("Database.csv");
                Scanner input = null;
                try {
                    input = new Scanner(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String line = "";
                while(input.hasNextLine()) {
                    line += input.nextLine() + "\n";
                }
                input.close();
                String[] sp = line.split(",");
                Scanner s = new Scanner(line).useDelimiter("\n");
                s.nextLine(); //skip header
                Boolean key = false;
                if(nameField.getText().contains("@")){
                    //email - password combination
                    while(s.hasNextLine()){
                        String[] words = s.nextLine().split(",");
                        if(nameField.getText().equals(words[1]) && passwordField.getText().equals(words[3])){
                            key = true;
                        }
                    }
                }
                else {
                    //username - password combination
                    while (s.hasNextLine()) {
                        String[] words = s.nextLine().split(",");
                        if (nameField.getText().equals(words[2]) && passwordField.getText().equals(words[3])) {
                            key = true;
                        }
                    }
                }
                if(key)
                {
                    showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Login Successful!", "Welcome " + nameField.getText());
                    String Username =nameField.getText();
                    try {
                        System.out.println("With User:" + Username);
                        Client client = new Client("localhost", 9001, Username);
                        Thread ClientThread = new Thread(client);
                        ClientThread.setDaemon(true);
                        ClientThread.start();
                        threads.add(ClientThread);
                        primaryStage.close();
                        primaryStage.setScene(initMainPane(client));
                        primaryStage.show();

                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else{
                    //TODO: DISPLAY WARNING
                    System.out.println("INVALID USERNAME/PASSWORD");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }


    private Scene initMainPane(Client client)
    {

         int width = 900;
        int height = 650;

         ArrayList<String> contacts = new ArrayList<String>();
        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(contacts));

        BorderPane mainPane = new BorderPane();

        ScrollPane contactScroll = new ScrollPane();

         VBox chatBox = new VBox(5);

         ScrollPane chatScroll = new ScrollPane();

         HBox FieldAndButton = new HBox(5);

        VBox contactPane = new VBox(10);

         Scene scene = new Scene(mainPane,width,height);
/////////////////////////////////////////////////////////////////////////////////////////////

        mainPane.setPadding(new Insets(10,10,10,10));
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());

/////////////////////////////////////////////////////////////////////////////////////////////
        contactPane.prefHeightProperty().bind(chatScroll.heightProperty());
        contactPane.setPadding(new Insets(0,0,0,5));
        contactPane.setAlignment(Pos.CENTER);
        TextField addUserIDField = new TextField();
        Label addContactLabel = new Label("Enter User ID:", addUserIDField);
        addContactLabel.setContentDisplay(ContentDisplay.BOTTOM);

        addUserIDField.prefWidthProperty().bind(listView.widthProperty());
        contactPane.getChildren().addAll(addContactLabel, listView);

/////////////////////////////////////////////////////////////////////////////////////////////

        chatBox.setPadding(new Insets(5,5,0,5));
        chatScroll.vvalueProperty().bind(chatBox.heightProperty());
        chatScroll.setContent(chatBox);

/////////////////////////////////////////////////////////////////////////////////////////////

        TextArea textfield = new TextArea();
        textfield.setWrapText(true);
        textfield.setPrefRowCount(5);
        textfield.prefWidthProperty().bind(scene.widthProperty().subtract(80));

        Button sendButton = new Button("Send");

        FieldAndButton.setAlignment(Pos.CENTER_LEFT);
        FieldAndButton.setPadding(new Insets(10,0,10,0));
        FieldAndButton.setAlignment(Pos.CENTER);
        FieldAndButton.getChildren().addAll(textfield,sendButton);

/////////////////////////////////////////////////////////////////////////////////////////////

        listView.prefHeightProperty().bind(chatScroll.heightProperty().subtract(50));

/////////////////////////////////////////////////////////////////////////////////////////////

        textfield.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
            {

                System.out.println("Message:"+textfield.getText());
                client.writeToServer(textfield.getText());
                textfield.clear();
                System.out.println("Outputing to server: " + client.chatLog);
                /*
                if(!textfield.getText().equals(""))
                {
                    System.out.println(textfield.getText());
                    Text message = new Text("You :  " + textfield.getText());
                    message.wrappingWidthProperty().bind(chatScroll.widthProperty().subtract(25));
                    textfield.setText("");
                    chatBox.getChildren().add(message);
                }
                */


                /*
                VBox ChatBox = new VBox(5);
                ObservableList<String> Log = client.chatLog;
                System.out.println(Log);
                for(String Msg : Log)
                {
                    Text Message = new Text( Msg);
                    Message.wrappingWidthProperty().bind(chatScroll.widthProperty().subtract(25));
                    ChatBox.getChildren().add(Message);


                }
                */
                e.consume();
            }
        });


/////////////////////////////////////////////////////////////////////////////////////////////

        sendButton.setOnAction(e -> {
            System.out.println(textfield.getText());
            Text message = new Text("You :  " + textfield.getText());
            message.wrappingWidthProperty().bind(chatScroll.widthProperty().subtract(25));
            textfield.setText("");
            chatBox.getChildren().add(message);
        });

/////////////////////////////////////////////////////////////////////////////////////////////

        listView.getSelectionModel().selectedItemProperty().addListener(
                ov -> {
                    chatBox.getChildren().clear();
                    //getHistory(String UserID);
                });
/////////////////////////////////////////////////////////////////////////////////////////////

        addUserIDField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if(!addUserIDField.getText().equals(""))
                {
                    System.out.println("User " + addUserIDField.getText() + " added.");
                    //addContact(addUserIDField.getText(),contacts,);
                    addUserIDField.clear();
                }
                e.consume();
            }
        });

/////////////////////////////////////////////////////////////////////////////////////////////


        ListView<String> history = new ListView<>();
        history.setItems(client.chatLog);
        System.out.println("Adding stuff to scroll Pane");
        System.out.println("Current Log:"+client.chatLog);



        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(history);
        mainPane.setCenter(history);
        mainPane.setRight(contactPane);
        mainPane.setBottom(FieldAndButton);
       // mainPane.setCenter(chatScroll);

        return scene;

/////////////////////////////////////////////////////////////////////////////////////////////
    }

   /*
    private void addContact(String name,ArrayList<String> contacts,ListView<ObservableList> listView)
    {
        contacts.add(name);
        listView.getItems().clear();
        listView.getItems().addAll(contacts);
    }
    */



    //@Override
    public void start(Stage primaryStage) {
        //initMainPane();
        threads =new ArrayList<Thread>();
        primaryStage.setScene(LoginScene(primaryStage));
        primaryStage.setTitle("ChatsApp");
        primaryStage.show();


    }
}

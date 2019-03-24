package chat_app;

//import java.awt.*;
import java.io.*;
import java.util.*;

import chat_app.server.Client;
import chat_app.server.Message;
import chat_app.server.User;
import chat_app.server.messagetype;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;


public class chatUI extends Application {

    private ArrayList<Thread> threads;



    public static void main(String[] args) {
        launch(args);
    }


    protected Scene LoginScene(Stage primaryStage) {

        log_in l = new log_in();

        double WIDTH = 800;
        double HEIGHT = 500;
        // Instantiate a new Grid Pane
        GridPane gridPane = l.LoginPane();
        Scene scene = new Scene(gridPane, WIDTH, HEIGHT);
        addUIControls(gridPane, primaryStage);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("log_in.css").toExternalForm());
        return scene;
    }

    private Scene RegScene(Stage primaryStage) {

        //register scene
        register r = new register();
        GridPane grid = r.generateRegPage(primaryStage, LoginScene(primaryStage));
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 400, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("register.css").toExternalForm());
        return scene;


    }


    private void addUIControls(GridPane gridPane, Stage primaryStage) {
        // Add Header
        Label headerLabel = new Label("CHATSAPP");
        headerLabel.setId("header");
        gridPane.add(headerLabel, 0, 0, 2, 1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));

        // Add Name Label
        Label nameLabel = new Label("Username : ");
        gridPane.add(nameLabel, 0, 1);

        // Add Name Text Field
        TextField nameField = new TextField();
        nameField.setPrefHeight(40);
        gridPane.add(nameField, 1, 1);

        // Add Password Label
        Label passwordLabel = new Label("Password : ");
        gridPane.add(passwordLabel, 0, 3);

        // Add Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPrefHeight(40);
        gridPane.add(passwordField, 1, 3);

        // Add Login Button
        Button loginButton = new Button("Login");
        loginButton.setId("loginButton");
        loginButton.setPrefHeight(40);
        loginButton.setPrefWidth(100);
        loginButton.setDefaultButton(true);
        gridPane.add(loginButton, 0, 4, 2, 1);
        GridPane.setHalignment(loginButton, HPos.CENTER);
        GridPane.setMargin(loginButton, new Insets(20, 0, 20, 150));

        // Add Register Button
        Button regButton = new Button("Register");
        regButton.setId("registerButton");
        regButton.setPrefHeight(40);
        regButton.setPrefWidth(100);
        regButton.setDefaultButton(false);
        gridPane.add(regButton, 0, 4, 2, 1);
        GridPane.setHalignment(regButton, HPos.CENTER);
        GridPane.setMargin(regButton, new Insets(20, 150, 20, 0));

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (nameField.getText().isEmpty()) {
                    Alert nameEmpty = showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", "Please enter your username");
                    nameEmpty.show();
                    return;
                }
                if (passwordField.getText().isEmpty()) {
                    Alert passEmpty = showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", "Please enter your password");
                    passEmpty.show();
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
                while (input.hasNextLine()) {
                    line += input.nextLine() + "\n";
                }
                input.close();
                String[] sp = line.split(",");
                Scanner s = new Scanner(line).useDelimiter("\n");
                s.nextLine(); //skip header
                Boolean key = false;
                if (nameField.getText().contains("@")) {
                    //email - password combination
                    while (s.hasNextLine()) {
                        String[] words = s.nextLine().split(",");
                        if (nameField.getText().equals(words[1]) && passwordField.getText().equals(words[3])) {
                            key = true;
                            nameField.setText(words[2]); //set to username
                        }
                    }
                } else {
                    //username - password combination
                    while (s.hasNextLine()) {
                        String[] words = s.nextLine().split(",");
                        if (nameField.getText().equals(words[2]) && passwordField.getText().equals(words[3])) {
                            key = true;
                        }
                    }
                }
                if (key) {
                    Alert r = showAlert(Alert.AlertType.CONFIRMATION, gridPane.getScene().getWindow(), "Login Successful!", "Welcome " + nameField.getText());
                    r.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            String Username = nameField.getText();
                            try {
                                System.out.println("With User:" + Username);
                                Client client = new Client("localhost", 9001, Username);
                                Thread ClientThread = new Thread(client);
                                ClientThread.setDaemon(true);
                                ClientThread.start();
                                threads.add(ClientThread);
                                //primaryStage.close();
                                primaryStage.setScene(initMainPane(client, primaryStage));
                                primaryStage.show();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            nameField.setText("");
                            passwordField.setText("");
                            return;
                        }
                    });
                } else {
                    //TODO: DISPLAY WARNING
                    Alert invalid = showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", "INVALID USERNAME/PASSWORD");
                    invalid.show();
                }
            }
        });

        regButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //redirects to register page
                primaryStage.setScene(RegScene(primaryStage));
            }
        });
    }

    private Alert showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        return alert;
    }


    private Scene initMainPane(Client client, Stage primaryStage) {
        /** Initial Variables */

        int width = 900;
        int height = 650;

        /** Contact listview */
        ArrayList<String> contacts = new ArrayList<String>();
        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(contacts));

        /** Pane for contact */
        BorderPane mainPane = new BorderPane();

        //VBox chatBox = new VBox(5);

        //ScrollPane chatScroll = new ScrollPane();

        //HBox FieldAndButton = new HBox(5);

        VBox contactPane = new VBox(10);

        Scene scene = new Scene(mainPane, width, height);


        /** mainPane properties */
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.prefHeightProperty().bind(scene.heightProperty());
        mainPane.prefWidthProperty().bind(scene.widthProperty());


        /** ContactPane properties */
        contactPane.prefHeightProperty().bind(scene.heightProperty().subtract(50));
        contactPane.setPadding(new Insets(0, 0, 0, 5));
        contactPane.setAlignment(Pos.CENTER);


        /** Auto complete field section */

        AutoCompleteTextField addUserIDField = new AutoCompleteTextField();

        File f = new File("Database.csv");
        Scanner input = null;
        try {
            input = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        while (input.hasNextLine()) {
            line += input.nextLine() + "\n";
        }
        input.close();

        String[] sp = line.split(",");

        Scanner s = new Scanner(line).useDelimiter("\n");
        s.nextLine(); //skip header
        ArrayList<String> suggestionList = new ArrayList<String>();
        while (s.hasNextLine()) {
            String[] words = s.nextLine().split(",");
            if (!client.getName().equals(words[2])) suggestionList.add(words[2]);
            //if(nameField.getText().equals(words[1]) && passwordField.getText().equals(words[3])){

        }
        addUserIDField.getEntries().addAll(suggestionList);
        listView.prefWidthProperty().bind(addUserIDField.prefWidthProperty().subtract(20));
        Label addContactLabel = new Label("Enter User ID:", addUserIDField);
        addContactLabel.setContentDisplay(ContentDisplay.BOTTOM);

        //addUserIDField.prefWidthProperty().bind(listView.widthProperty());
        contactPane.getChildren().add(addContactLabel);

        //TODO: 2303 CHANGES
        //make a button that allows adding members to group
        Button grp = new Button("Add Group");
        Button setgrp = new Button("Set Group Chat");
        contactPane.getChildren().addAll(grp,setgrp);

        contactPane.getChildren().add(listView);

        //Create a list for users selected into groups
        List<String> selected = new ArrayList<>();



        grp.setOnAction(e->{

            //Add checkbox to each cell
            listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
                @Override
                public ObservableValue<Boolean> call(String item) {
                    BooleanProperty observable = new SimpleBooleanProperty();
                    observable.addListener((obs, wasSelected, isNowSelected) -> {
                                if(isNowSelected) {
                                    Boolean k = true;
                                    for(String name:selected){
                                        if(name.equals(item.trim())){
                                            System.out.println("You're false????");
                                            k = false;
                                        }
                                    }
                                    if(k) selected.add(item.trim());
                                    System.out.println(item + " is selected");
                                }
                                else{
                                    selected.remove(item.trim());
                                    System.out.println(item + " is NOT selected");
                                }
                            }
                    );
                    return observable ;
                }
            }));


            System.out.println("Size of selected:" + selected.size());
            //System.out.println("Selected users are:");
            //for(String n:selected){
                //System.out.print(n);
            //}
        });


        setgrp.setOnAction(e->{
            ArrayList<String> Users = new ArrayList<>();
            //If no user is selected, don't do anything
            if(selected.size() < 1){return;}
            else if(selected.size() == 1){ //if only 1 user is selected, fetch pane with that user
                Users.add(selected.get(0)); //Get the only other person there
                System.out.println(">>> Chatting With:" + selected.get(0));
                mainPane.setCenter(getChatPane(client, Users));
            }
            else {
                //if multiple user is selected, pane generate yadda yadda
                for (String u : selected) Users.add(u);
                mainPane.setCenter(getChatPane(client, Users));
                Users = new ArrayList<>();
            }
            contactPane.getChildren().remove(grp);
            //selected.clear();
            //listView.getItems().clear();
            //listView.getItems().addAll(contacts);


        });


        //TODO: END OF 2303 CHANGES

        /**chatBox properties */


        /**FieldAndButton properties */

        TextArea textfield = new TextArea();
        textfield.setWrapText(true);
        textfield.setPrefRowCount(5);
        textfield.setEditable(true);
        textfield.prefWidthProperty().bind(scene.widthProperty().subtract(80));

        //Button sendButton = new Button("Send");
        //sendButton.setId("sendButton");

        //FieldAndButton.setAlignment(Pos.CENTER_LEFT);
        //FieldAndButton.setPadding(new Insets(10, 0, 10, 0));
        //FieldAndButton.setAlignment(Pos.CENTER);
        //FieldAndButton.getChildren().addAll(textfield, sendButton);

        /**Contact listView */

        listView.prefHeightProperty().bind(scene.heightProperty().subtract(170));
        listView.getSelectionModel().selectedItemProperty().addListener(
                ov -> {
                    String name = listView.getSelectionModel().getSelectedItem();
                    System.out.println("Chatting With:" + name);
                    ArrayList<String> Users = new ArrayList<>();
                    Users.add(name);
                    mainPane.setCenter(getChatPane(client, Users));
                    //THIS CAUSES THINGS TO BE SLIIIIIGHTLY BUGGY in terms of checkbox vs textfield select, BUT IT'S THE LEAST BUGGY OF THEM ALL XD
                    selected.clear();
                });

        /**Event handlings */

        textfield.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && textfield.getText().trim().equals("")) {
                //No blank message
                System.out.println("BLANK MESSAGE I WON'T LET YOU!!");
            } else if (e.getCode() == KeyCode.ENTER && !textfield.getText().trim().equals("")) {
                //Write message
                System.out.println("Message:" + textfield.getText());
                client.writeToServer(textfield.getText());
                ArrayList<String> Users = new ArrayList<>();
                Users.add("null"); //TODO: ADD USER TO BE IMPLEMENTED
                client.UpdateServer(textfield.getText(), Users);
                Message m = new Message(client.getName(), Users, textfield.getText().trim());
                String json = m.toJson();
                String fileName = "json.csv";
                File jsonF = new File(fileName);
                jsonHandler j = new jsonHandler(fileName, json);
                try {
                    if (!jsonF.exists()) {
                        j.generateHeader();
                    }
                    j.writeJson();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                textfield.clear();
                System.out.println("Outputting to server: " + client.chatLog);
                e.consume();
            }
        });

        addUserIDField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                boolean contactExist = false;
                for (String i : contacts) {
                    if (addUserIDField.getText().equals(i)) {
                        contactExist = true;
                        break;
                    }
                }
                if (!addUserIDField.getText().equals("") && !addUserIDField.getText().equals(client.getName()) && !contactExist) {
                    File file = new File("Database.csv");
                    Scanner in = null;
                    try {
                        in = new Scanner(file);
                    } catch (FileNotFoundException er) {
                        er.printStackTrace();
                    }
                    String l = "";
                    while (in.hasNext()) {
                        l += in.nextLine() + "\n";
                    }
                    in.close();
                    String[] s_arr = l.split(",");
                    Scanner scan = new Scanner(l).useDelimiter("\n");
                    scan.nextLine();
                    while (scan.hasNextLine())
                    {
                        String[] word = scan.nextLine().split(",");
                        if (addUserIDField.getText().equals(word[2])) {
                            System.out.println("User " + addUserIDField.getText() + " added.");
                            //addContact(addUserIDField.getText(),contacts);
                            //contacts.add(addUserIDField.getText());
                            selected.clear();
                            listView.getItems().add(addUserIDField.getText());
                            //listView.getItems().addAll(contacts);
                            addUserIDField.clear();
                            break;
                        }
                    }
                }
                e.consume();
            }
        });
        input.close();


        //ListView<String> history = new ListView<>();


        //ScrollPane scrollPane = new ScrollPane();
        //scrollPane.setContent(history);

        /**Menubar section*/
        Menu account = new Menu("Account");
        MenuItem logout = new MenuItem("Logout");

        account.getItems().addAll(logout);

        MenuBar mb = new MenuBar();
        mb.getMenus().add(account);

        logout.setOnAction(e -> {
            Scene scene1 = LoginScene(primaryStage);

            Alert r = showAlert(Alert.AlertType.CONFIRMATION, primaryStage.getScene().getWindow(), "Logout", "Are you sure you want to log out?");
            r.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    primaryStage.setScene(scene1);
                } else {
                    return;
                }
            });
        });


        mainPane.setTop(mb);
        mainPane.setRight(contactPane);
        mainPane.setMargin(mb, new Insets(5));
        scene.getStylesheets().add(getClass().getClassLoader().getResource("chat.css").toExternalForm());

        return scene;

/////////////////////////////////////////////////////////////////////////////////////////////
    }

    private Pane getChatPane(Client client, ArrayList<String> Users)
    {
        BorderPane pane = new BorderPane();
        TextArea entry = new TextArea();

        entry.setWrapText(true);
        entry.setPrefRowCount(5);
        entry.setEditable(true);

        HBox hbox = new HBox(5);
        VBox vbox = new VBox(5);
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);

        Button send = new Button();
        ImageView sendImage = new ImageView(new Image("/img/send.png"));
        sendImage.setFitWidth(33);
        sendImage.setFitHeight(27);
        send.setGraphic(sendImage);


        Button image = new Button(); // Would Like to convert this to an Image Button
        ImageView imageImage = new ImageView(new Image("/img/image.png"));
        imageImage.setFitWidth(33);
        imageImage.setFitHeight(33);
        image.setGraphic(imageImage);

        Button voice = new Button();
        ImageView voiceImage = new ImageView(new Image("/img/voice.jpg"));
        voiceImage.setFitWidth(33);
        voiceImage.setFitHeight(33);
        voice.setGraphic(voiceImage);

        vbox.getChildren().addAll(image, voice);
        hbox.getChildren().addAll(vbox, entry, send);
        pane.setMargin(hbox, new Insets(10));


        ListView<String> listView = new ListView<>();
        listView.setItems(client.chatLog);
        jsonHandler handler = new jsonHandler("json.csv");


        /**Testing listview */
        ArrayList<String> messagesBox = new ArrayList<String>();
        ListView<String> messageScroll = new ListView<>(FXCollections.observableArrayList(messagesBox));

        client.chatLog.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded())
                {
                    ListView<String> messageScrollblank = new ListView<>();
                    pane.setCenter(messageScrollblank);
                    try {
                        for (Message msg : handler.filterCertainUsers(Users)) {

                            //TODO: Use Create History to read the past messages

                            //TODO: Check Messages


                            //System.out.println(item);
                            //TODO: Use Parser for reading through each item.


                            String Client = msg.getClientName();
                            String Mess = msg.getMessage();
                            if(Mess.contains("[-c-]")){
                                Mess = Mess.replace("[-c-]",",");
                            }
                            String user = Users.get(0);
                            ArrayList<String> recipients = msg.getUsers();

                            //System.out.println("Users:" + Users);

                            //TODO: 2303 CHANGES
                            //let's first parse the 'x' out
                            //and then we use for loop to check each one
                            String[] msgsessionIDx_parsed = msg.getSession_ID().trim().split("x");
                            String cursessionID = getSessionID(client, Users);
                            String[] cursessionID_parsed = getSessionID(client, Users).trim().split("x");

                            Boolean key = true;
                            for(String id:msgsessionIDx_parsed){
                                if(cursessionID_parsed.length != msgsessionIDx_parsed.length || !cursessionID.contains(id)){
                                    key=false;
                                    break;
                                }
                            }

                            if (key) {
                                //System.out.println("1We are communicating with Session: " + msg.getSession_ID() + " Client: " + Client + " and " + user + " Message: " + Mess);
                                //String messageformat = client.getName() + ": " + entry.getText();
                                //System.out.println(Mess);

                                // message.setStyle("-fx-text-inner-color: yellow;");
                                //Font font = new Font("Times New Roman", 10, Color.YELLOW)

                                // TODO: Left and right message Text

                                if(msg.getSession_ID().charAt(0)==(getSessionID(client,Users).charAt(0)))
                                {

                                    //From the person that is logged in
                                    System.out.println(" Session_ID from Logs:" +msg.getSession_ID());
                                    System.out.println("Message from me: " + Mess + " to " + recipients + "\n");

                                    //Client is sender
                                    //String messageformat = client.getName() + " : " + Mess
                                    //Label label=new Label("guru ");
                                    String messageFormat = "Me : " + Mess;
                                    //message.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14));
                                    //message.setFill(Color.BLUE);
                                    //label.getStylesheets().add("sample/styles/send.css");
                                    //message.setId("receive");
                                    //HBox hBox=new HBox();
                                    //hBox.getChildren().add(message);
                                    //hBox.setAlignment(Pos.CENTER_RIGHT);
                                    //message.wrappingWidthProperty().bind(messageScroll.widthProperty().subtract(25));


                                    messagesBox.add(messageFormat);
                                    //messageScroll.getItems().clear();

                                    messageScrollblank.getItems().add(messageFormat);

                                    //messageScroll.getItems().addAll(messagesBox);
                                    messageScrollblank.scrollTo(messagesBox.size()-1);

                                }
                                else
                                {

                                    //From anyone else but the person that is logged in
                                    System.out.println(" Session_ID from Logs:" +msg.getSession_ID());
                                    System.out.println("Message from " + Client + ": " + Mess + "\n");

                                    //Client is receiver
                                    String messageFormat = Client +" : " +Mess;
                                    //label.getStylesheets().add("sample/styles/send.css");
                                    //message.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14));
                                    //message.setFill(Color.RED);
                                    //message.setId("send");
                                    //HBox hBox=new HBox();
                                    //hBox.getChildren().add(message);
                                    //hBox.setAlignment(Pos.CENTER_LEFT);
                                    // messagesBox.getChildren().add(message);
                                    //message.wrappingWidthProperty().bind(messageScroll.widthProperty().subtract(25));
                                    //messagesBox.setSpacing(10);

                                    messagesBox.add(messageFormat);
                                    //messageScroll.getItems().clear();

                                    messageScrollblank.getItems().add(messageFormat);

                                    //messageScroll.getItems().addAll(messagesBox);
                                    messageScrollblank.scrollTo(messagesBox.size()-1);

                                }
                                //messageScroll.refresh();


                                //messagesBox.getChildren().add(message);
                                //messagesBox.getChildren().add()
                            }
                            //TODO: Extract Msg and create View Box for the Users for Terry
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                } /*else if (change.wasRemoved()) {
                    System.out.println(change.getRemoved().get(0)
                            + " was removed from the list!");
                }*/
            }
        });

        //messageScroll.setPannable(true);
        //messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //messagesBox.setPadding(new Insets(5,5,0,5));

        //messageScroll.setFitToWidth(true);
        //messageScroll.setContent(messagesBox);

        //messageScroll.vvalueProperty().bind(messagesBox.heightProperty());


        //Actions for send button
        send.setOnAction(e -> {
            if (!entry.getText().equals("")) {
                System.out.println("Message:" + entry.getText());
                String entrystr = entry.getText();
                //parse comma save as "[-c-]"
                if(entrystr.contains(",")){
                    entrystr = entrystr.replace(",","[-c-]");
                }
                Message m = new Message(client.getName(), Users, entry.getText().trim(),getSessionID(client,Users));
                client.UpdateMessage(m);

                String json = m.toJson();
                String fileName = "json.csv";
                File jsonF = new File(fileName);
                jsonHandler j = new jsonHandler(fileName, json);
                try {
                    if (!jsonF.exists()) {
                        j.generateHeader();
                    }
                    j.writeJson();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                entry.clear();
                System.out.println("Outputting to server: " + client.chatLog);

            }
        });

        entry.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !entry.getText().trim().equals("")) {
                //If there's a message sent, save it to json (no prob)
                String entrystr = entry.getText();
                System.out.println("Message:" + entrystr);
                //parse comma save as "[-c-]"
                if(entrystr.contains(",")){
                    entrystr = entrystr.replace(",","[-c-]");
                }
                Message m = new Message(client.getName(), Users, entrystr.trim(),getSessionID(client,Users));
                client.UpdateMessage(m);

                String json = m.toJson();
                String fileName = "json.csv";
                File jsonF = new File(fileName);
                jsonHandler j = new jsonHandler(fileName, json);
                try {
                    if (!jsonF.exists()) {
                        j.generateHeader();
                    }
                    j.writeJson();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                entry.clear();
                System.out.println("Outputting to server: " + client.chatLog);
            }
            e.consume();
        });

        // Actions for Image button
        image.setOnAction(event -> {
            Message Msg = new Message(client.getName(), Users, "Image");
            Msg.SetType(messagetype.IMAGE);
            //OutputStream os ;
            System.out.println("Sending Image message");
            Alert imageImplement = showAlert(Alert.AlertType.ERROR, vbox.getScene().getWindow(), "Oops!", "Under development :)");
            imageImplement.show();
            return;


        });


        // Actions for Voice button
        voice.setOnAction(event -> {
            Message Msg = new Message(client.getName(), Users, "Image");
            Msg.SetType(messagetype.VOICE);
            System.out.println("Sending Voice message");
            Alert imageImplement = showAlert(Alert.AlertType.ERROR, vbox.getScene().getWindow(), "Oops!", "Under development :)");
            imageImplement.show();
            return;
            //OutputStream os ;


        });


        try {
            for (Message msg : handler.filterCertainUsers(Users)) {



                //TODO: Use Create History to read the past messages

                //TODO: Check Messages


                //System.out.println(item);
                //TODO: Use Parser for reading through each item.


                String Client = msg.getClientName();
                String Mess = msg.getMessage();
                if(Mess.contains("[-c-]")){
                    Mess = Mess.replace("[-c-]",",");
                }
                String user = Users.get(0);
                ArrayList<String> recipients = msg.getUsers();

                //System.out.println("Users:" + Users);

                //TODO: 2303 CHANGES
                //let's first parse the 'x' out
                //and then we use for loop to check each one
                String[] msgsessionIDx_parsed = msg.getSession_ID().trim().split("x");
                String cursessionID = getSessionID(client, Users);
                String[] cursessionID_parsed = getSessionID(client, Users).trim().split("x");
                //System.out.println("From getSessionID, SessionID: " + cursessionID);

                Boolean key = true;
                for(String id:msgsessionIDx_parsed){
                    if(cursessionID_parsed.length != msgsessionIDx_parsed.length || !cursessionID.contains(id)){
                        key=false;
                        break;
                    }
                }
                if (key) {
                    //System.out.println("2We are communicating with Session: " + msg.getSession_ID() + " Client: " + Client + " and " + user + " Message: " + Mess);
                    //String messageformat = client.getName() + ": " + entry.getText();
                    //System.out.println(Mess);

                    // message.setStyle("-fx-text-inner-color: yellow;");
                    //Font font = new Font("Times New Roman", 10, Color.YELLOW)

                    // TODO: Left and right message Text

                    if(msg.getSession_ID().charAt(0)==(getSessionID(client,Users).charAt(0)))
                    {

                        //From the person that is logged in
                        System.out.println(" Session_ID from Logs:" +msg.getSession_ID());
                        System.out.println("Message from me: " + Mess + " to " + recipients + "\n");

                        //Client is sender
                        //String messageformat = client.getName() + " : " + Mess
                        //Label label=new Label("guru ");
                        String messageFormat = "Me : " + Mess;
                        //message.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14));
                        //message.setFill(Color.BLUE);
                        //label.getStylesheets().add("sample/styles/send.css");
                        //message.setId("receive");
                        //HBox hBox=new HBox();
                        //hBox.getChildren().add(message);
                        //hBox.setAlignment(Pos.CENTER_RIGHT);
                        //message.wrappingWidthProperty().bind(messageScroll.widthProperty().subtract(25));

                        messagesBox.add(messageFormat);
                        messageScroll.getItems().clear();
                        messageScroll.getItems().addAll(messagesBox);
                        messageScroll.scrollTo(messagesBox.size()-1);

                    }
                    else
                    {
                        //From anyone else but the person that is logged in
                        System.out.println(" Session_ID from Logs:" +msg.getSession_ID());
                        System.out.println("Message from " + Client + ": " + Mess + "\n");


                        //Client is receiver
                        String messageFormat = Client +" : " +Mess;
                        //label.getStylesheets().add("sample/styles/send.css");
                        //message.setFont(Font.font("Verdana", FontWeight.MEDIUM, 14));
                        //message.setFill(Color.RED);
                        //message.setId("send");
                        //HBox hBox=new HBox();
                        //hBox.getChildren().add(message);
                        //hBox.setAlignment(Pos.CENTER_LEFT);
                       // messagesBox.getChildren().add(message);
                        //message.wrappingWidthProperty().bind(messageScroll.widthProperty().subtract(25));
                        //messagesBox.setSpacing(10);

                        messagesBox.add(messageFormat);
                        messageScroll.getItems().clear();
                        messageScroll.getItems().addAll(messagesBox);
                        messageScroll.scrollTo(messagesBox.size()-1);

                    }
                    //messageScroll.refresh();


                    //messagesBox.getChildren().add(message);
                    //messagesBox.getChildren().add()
                }
                //TODO: Extract Msg and create View Box for the Users for Terry
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        entry.prefWidthProperty().bind(messageScroll.widthProperty().subtract(130));
/*
        pane.add(listView,0,0);
        pane.add(entry,0,3);
        pane.add(send, 3,3);
        pane.add(Image,3,4);
        pane.add(Voice,3,5);
*/

        //TODO: 2303 CHANGES
        String chatTitle = "Chatting with ";
        if(Users.size() == 1) chatTitle += Users.get(0);
        else {
            for (int i = 0; i < Users.size(); i++) {
                if (i + 1 == Users.size()) chatTitle += "and " + Users.get(i);
                else chatTitle += Users.get(i) + " ";
            }
        }
        Text chattingWith = new Text(chatTitle);
        //TODO: END 2303 CHANGES
        chattingWith.setFont(Font.font("Verdana", FontWeight.MEDIUM, 17));
        chattingWith.setFill(Color.WHITE);
        pane.setTop(chattingWith);
        pane.setCenter(messageScroll);
        pane.setBottom(hbox);
        return pane;
    }


    private String getSessionID(Client client, ArrayList<String> users)
    {
        String Session_ID = "";

        File file = new File("Database.csv");
        String left_id = "";
        String right_id = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] contents = line.split(",");
                if (contents[2].trim().equals(client.getName())) {
                    left_id = contents[0];
                }
                for (String usr : users) {
                    if (contents[2].trim().equals(usr)) {
                        right_id += String.format("x" + contents[0]);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        Session_ID += left_id + right_id;
        //System.out.println("From getSessionID, SessionID: " + Session_ID);
        return Session_ID;


    }

    public  String reverse(String s)
    {
        String output = "";
        for(int i = s.length()-1; i >= 0 ; i--)
        {
            output += s.charAt(i);
        }
        System.out.println("Reverse: " + output);
        return  output;
    }

    //@Override
    public void start(Stage primaryStage) {
        //initMainPane();
        threads = new ArrayList<Thread>();
        primaryStage.setScene(LoginScene(primaryStage));
        primaryStage.setTitle("ChatsApp");
        primaryStage.show();


    }


}

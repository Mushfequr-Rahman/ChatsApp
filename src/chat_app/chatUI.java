package chat_app;

import java.util.ArrayList;

import chat_app.server.Client;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class chatUI extends Application
{

    private ArrayList<Thread> threads;



    public static void main(String[] args) {
        launch(args);
    }



    private Scene LogIn(Stage primaryStage)
    {


        GridPane gridPane = new GridPane();
        TextField text = new TextField();
        Button login = new Button("Login");
        gridPane.add(text,0,0);
        gridPane.add(login,1,0);

        login.setOnAction(e->{
            String Username = text.getText();
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
        });

        return new Scene(gridPane,300,300);
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
        primaryStage.setScene(LogIn(primaryStage));
        primaryStage.setTitle("ChatsApp");
        primaryStage.show();


    }
}

package chat_app;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
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
    private int width = 900;
    private int height = 650;

    private ArrayList<String> contacts = new ArrayList<String>();
    private ListView<String> listView = new ListView<>(FXCollections.observableArrayList(contacts));

    private BorderPane mainPane = new BorderPane();

    private ScrollPane contactScroll = new ScrollPane();

    private VBox chatBox = new VBox(5);

    private ScrollPane chatScroll = new ScrollPane();

    private HBox FieldAndButton = new HBox(5);

    private VBox contactPane = new VBox(10);

    private  Scene scene = new Scene(mainPane,width,height);



    public static void main(String[] args) {
        launch(args);
    }

    private void initMainPane()
    {
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
                if(!textfield.getText().equals(""))
                {
                    System.out.println(textfield.getText());
                    Text message = new Text("You :  " + textfield.getText());
                    message.wrappingWidthProperty().bind(chatScroll.widthProperty().subtract(25));
                    textfield.setText("");
                    chatBox.getChildren().add(message);
                }
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
                    addContact(addUserIDField.getText());
                    addUserIDField.clear();
                }
                e.consume();
            }
        });

/////////////////////////////////////////////////////////////////////////////////////////////

        mainPane.setRight(contactPane);
        mainPane.setBottom(FieldAndButton);
        mainPane.setCenter(chatScroll);

/////////////////////////////////////////////////////////////////////////////////////////////
    }
    private void addContact(String name)
    {
        contacts.add(name);
        listView.getItems().clear();
        listView.getItems().addAll(contacts);
    }

    //@Override
    public void start(Stage primaryStage) {
        initMainPane();
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChatsApp");
        primaryStage.show();


    }
}

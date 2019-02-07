package chat_app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class log_in extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("log_in.fxml"));


        primaryStage.setTitle("Log In ");
        primaryStage.setScene(new Scene(root,500,400));
        primaryStage.show();

        GridPane grid = generateLoginPage();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25,25,25,25));
        Scene scene = new Scene(grid,400,400);
        primaryStage.setScene(scene);






    }

    private GridPane generateLoginPage() {

        GridPane LogIn = new GridPane();

        javafx.scene.text.Text name_prompt = new Text("Username:");
        LogIn.add(name_prompt, 0,1 );

        TextField username_entry = new TextField();
        LogIn.add(username_entry,1,1);

        Text password_prompt = new Text("Password:");

        LogIn.add(password_prompt,0,2);

        PasswordField password_entry = new PasswordField();
        LogIn.add(password_entry,1,2);

        Button log_in_button = new Button("Log In");
        LogIn.add(log_in_button,1,3);

        Button Register_button = new Button("Register");
        LogIn.add(Register_button,2,3);


        log_in_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                BeginLogIn(username_entry.getText(),password_entry.getText());
            }
        });

        Register_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Begin_Registration();
            }
        });





        return LogIn;


    }

    private void BeginLogIn(String text, String text1) {

        //TODO: Set Up User Verification
    }

    private void Begin_Registration() {

        //TODO: Create Registration Event and Classes
    }


}

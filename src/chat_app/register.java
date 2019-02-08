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

public class register extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        primaryStage.setTitle("Registration Page ");
        primaryStage.show();

        GridPane grid = generateRegPage();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private GridPane generateRegPage() {

        GridPane Reg = new GridPane();

        javafx.scene.text.Text username_prompt = new Text("Username:");
        Reg.add(username_prompt, 0, 1);

        TextField username_entry = new TextField();
        Reg.add(username_entry, 1, 1);

        javafx.scene.text.Text email_prompt = new Text("Email Address:");
        Reg.add(email_prompt, 0, 2);

        TextField email_entry = new TextField();
        Reg.add(email_entry, 1, 2);

        Text password_prompt = new Text("Enter Password:");
        Reg.add(password_prompt, 0, 3);

        PasswordField password_entry = new PasswordField();
        Reg.add(password_entry, 1, 3);

        Text password_prompt2 = new Text("Re-enter Password:");
        Reg.add(password_prompt2, 0, 4);

        PasswordField password_entry2 = new PasswordField();
        Reg.add(password_entry2, 1, 4);

        Button Register_button = new Button("Register");
        Reg.add(Register_button, 1, 5);


        Register_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }

        });

        return Reg;
    }
}

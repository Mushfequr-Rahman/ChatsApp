package chat_app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

public class register extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane grid = generateRegPage();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registration Page ");
        primaryStage.show();

    }

    private GridPane generateRegPage() {

        GridPane Reg = new GridPane();

        javafx.scene.text.Text username_prompt = new Text("Username:"); //5-20 characters
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

                if(!validate(username_entry,email_entry,password_entry,password_entry2, Reg)){
                    /*
                    * TODO:
                    * >> VALIDATE AGAINST EXISTING USERNAMES
                    * >> VALIDATE AGAINST POSSIBLE COMMA EXCEPTION (POTENTIAL CSV PARSING BREAKING)
                    * */
                    return;
                }

                //Saving into csv
                String fileName= "sample.csv";
                FileWriter file;
                //txt to keep track of data easier
                String fileName2= "sample.txt";
                FileWriter f2;
                StringBuilder sb = new StringBuilder();
                try {
                    if (new File(fileName).exists()) {
                        //If file exist, simply append
                        System.out.println("File has existed please don't overwrite");
                        file = new FileWriter(new File(fileName), true);
                        f2 = new FileWriter(new File(fileName2), true);
                        //append function
                        sb.append(generateSb(getCSVID(new File(fileName)),email_entry,username_entry,password_entry));
                        //Write to file function
                        file.write(sb.toString());
                        f2.write(sb.toString());
                        file.close();
                        f2.close();
                    } else {
                        //Create header first
                        System.out.println("File has not existed, create new one!");
                        file = new FileWriter(new File(fileName), false);
                        f2 = new FileWriter(new File(fileName2), false);
                        sb.append("Id");
                        sb.append(',');
                        sb.append("Email Address");
                        sb.append(',');
                        sb.append("Username");
                        sb.append(',');
                        sb.append("Password");
                        sb.append(',');
                        sb.append("Timestamp");
                        sb.append('\n');
                        //append function
                        sb.append(generateSb(getCSVID(1),email_entry,username_entry,password_entry));
                        //Write to file function
                        file.write(sb.toString());
                        f2.write(sb.toString());
                        file.close();
                        f2.close();

                    }
                }catch (Exception e){
                    System.out.println("Dunno something caught");
                    System.exit(0);
                }

            }

        });

        return Reg;
    }

    Boolean validate(TextField u, TextField e, TextField p1, TextField p2, GridPane pane){
        //Check textfield values against possible issues
        Boolean key = true;
        if(!validateEmpty(u,e,p1,p2)){
            //TODO: DISPLAY NULL VALUE WARNING
            key = false;
        }
        if(!p1.getText().equals(p2.getText())){
            //TODO: DISPLAY MISMATCH PASSWORD WARNING
            System.out.println("PASS MISMATCH");
            key = false;
        }
        if(!validateLength(u,6,20)){
            //TODO: DISPLAY (USERNAME must be between 8-20 characters) WARNING (temporary req up to change)
            System.out.println("INSUFFICIENT LENGTH USERNAME");
            key = false;
        }
        if(!validateLength(p1,8,20)){
            System.out.println("INSUFFICIENT LENGTH PASSWORD");
            key = false;
            //TODO: DISPLAY (PASSWORD must be between 8-20 characters) WARNING (temporary req up to change)
        }
        if(!Pattern.matches("^(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", u.getText())){
            System.out.println("WRONG USERNAME FORMAT");
            key = false;
            //TODO: DISPLAY WRONG USERNAME FORMAT
        }
        if(!Pattern.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b", e.getText())){
            System.out.println("WRONG EMAIL FORMAT");
            key = false;
            //TODO: DISPLAY WRONG EMAIL FORMAT
        }
        return key;
    }

    Boolean validateEmpty(TextField u, TextField e, TextField p1, TextField p2){
        //Check for empty text fields
        if(u.getText().equals("") || e.getText().equals("") || p1.getText().equals("") || p2.getText().equals("")){
            System.out.println("ONE OR MORE COLUMNS ARE EMPTY");
            return false;
        }
        return true;
    }

    Boolean validateLength(TextField tf, int min, int max){
        //Check for length of string in textfield
        if(tf.getText().length() < min || tf.getText().length() > max) return false;
        return true;
    }




    String generateSb(String idStr,TextField e,TextField u,TextField p){
        //Generate stringbuilder in csv format
        String str = idStr;
        str += e.getText() + ","; //add email
        str += u.getText() + ","; //add username
        str += p.getText() + ","; //add password
        str += new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()); //add timestamp
        str += "\n";
        return str;
    }


    String getCSVID(int id){
        //Generate first ID(id=1) or given ID in csv format
        String str = "";
        str += Integer.toString(id) + ",";
        return str;
    }


    String getCSVID(File f) throws Exception{
        //Generate ID based on last saved in csv
        int id = getLastID(f);
        return getCSVID(id);
    }

    int getLastID(File f) throws Exception{
        //Read last line of file to create unique auto-increment ID
        Scanner input = new Scanner(f);
        String line = "";
        while(input.hasNextLine()) {
            line = input.nextLine();
        }
        String[] words = line.split(",");
        return Integer.parseInt(words[0])+ 1;
    }


}

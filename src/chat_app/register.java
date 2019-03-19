package chat_app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class register extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        GridPane grid = generateRegPage(null,null);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Scene scene = new Scene(grid, 400, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("register.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Registration Page ");
        primaryStage.show();

    }

    protected GridPane generateRegPage(Stage primaryStage, Scene s) {

        GridPane Reg = new GridPane();

        ImageView icon = new ImageView("img/icon.png");
        icon.setId("icon");
        Reg.getChildren().add(icon);


        Text username_prompt = new Text("Username:"); //5-20 characters
        username_prompt.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        username_prompt.setFill(Color.WHITE);
        Reg.add(username_prompt, 0, 1);

        TextField username_entry = new TextField();
        Reg.add(username_entry, 1, 1);

        Text email_prompt = new Text("Email Address:");
        email_prompt.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        email_prompt.setFill(Color.WHITE);
        Reg.add(email_prompt, 0, 2);

        TextField email_entry = new TextField();
        Reg.add(email_entry, 1, 2);

        Text password_prompt = new Text("Enter Password:");
        password_prompt.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        password_prompt.setFill(Color.WHITE);
        Reg.add(password_prompt, 0, 3);

        PasswordField password_entry = new PasswordField();
        Reg.add(password_entry, 1, 3);

        Text password_prompt2 = new Text("Re-enter Password:");
        password_prompt2.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        password_prompt2.setFill(Color.WHITE);
        Reg.add(password_prompt2, 0, 4);

        PasswordField password_entry2 = new PasswordField();
        Reg.add(password_entry2, 1, 4);

        Button Register_button = new Button("Register");
        Register_button.setId("register_button");
        Register_button.setFont(Font.font("Serif", FontWeight.BOLD, 18));
        Reg.add(Register_button, 1, 5);
        Register_button.setDefaultButton(true);


        Register_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                //Saving into csv
                String fileName= "Database.csv";
                FileWriter file;

                try {
                    if(!new File(fileName).exists()){
                        if (!validate(username_entry,email_entry,password_entry,password_entry2, Reg)) {
                            return;
                        }
                    }
                    else{
                        if (!validate(username_entry,email_entry,password_entry,password_entry2, Reg, new File(fileName))) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //txt to keep track of data easier
                String fileName2= "Database.txt";
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
                    chatUI c = new chatUI();
                    //"USER HAS BEEN REGISTERED" DISPLAY before redirect.
                    Alert registered = showAlert(Alert.AlertType.INFORMATION, Reg.getScene().getWindow(), "Success!", username_entry.getText() + " has been registered.");
                    registered.show();
                    primaryStage.setScene(c.LoginScene(primaryStage));
                }catch (Exception e){
                    System.out.println("Dunno something caught");
                    System.exit(0);
                }

            }

        });

        return Reg;
    }

    Boolean validate(TextField u, TextField e, TextField p1, TextField p2, GridPane pane, File f) throws Exception{
        //Check textfield values against possible issues
        Boolean key = true;
        if(!validateEmpty(u,e,p1,p2)){
            //DISPLAY NULL VALUE WARNING
            Alert empty = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "NULL VALUE");
            empty.show();
            key = false;
        }
        if(!p1.getText().equals(p2.getText())){
            //DISPLAY MISMATCH PASSWORD WARNING
            Alert passMismatch = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "PASSWORD MISMATCH");
            passMismatch.show();
            key = false;
        }
        if(!validateUser(u,f)){
            //ERROR SAME USERNAME CHOOSE ANOTHER ONE
            Alert overlap = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "OVERLAPPING USERNAME");
            overlap.show();
            key = false;
        }
        if(!validateEmail(e,f)){
            //ERROR SAME EMAIL USE ANOTHER ONE
            Alert sameEmail = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "EMAIL HAS BEEN USED ALREADY");
            sameEmail.show();
            key = false;
        }
        if(!validateLength(u,6,20)){
            //DISPLAY (USERNAME must be between 8-20 characters) WARNING (temporary req up to change)
            Alert userLength = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Username must be between 8-20 characters.");
            userLength.show();
            key = false;
        }
        if(!validateLength(p1,8,20)){
            //DISPLAY (PASSWORD must be between 8-20 characters) WARNING (temporary req up to change)
            Alert passLength = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Password must be between 8-20 characters.");
            passLength.show();
            key = false;
        }
        if(p1.getText().contains(",")){
            //DO NOT USE COMMA FOR PASSWORD THX
            Alert noComma = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Cannot use comma in password.");
            noComma.show();
            key = false;
        }
        if(!Pattern.matches("^(?![_.,])(?!.*[_.,]{2})[a-zA-Z0-9._]+(?<![_.,])$", u.getText())){
            //DISPLAY WRONG USERNAME FORMAT
            Alert userFormat = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "WRONG USERNAME FORMAT");
            userFormat.show();
            key = false;
        }
        if(!Pattern.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b", e.getText())){
            //DISPLAY WRONG EMAIL FORMAT
            Alert emailFormat = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "WRONG EMAIL FORMAT");
            emailFormat.show();
            key = false;
        }
        return key;
    }

    //overload: very first user give this user a trophy
    Boolean validate(TextField u, TextField e, TextField p1, TextField p2, GridPane pane){
        //Check textfield values against possible issues
        Boolean key = true;
        if(!validateEmpty(u,e,p1,p2)){
            //DISPLAY NULL VALUE WARNING
            Alert empty = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "NULL VALUE");
            empty.show();
            key = false;
        }
        if(!p1.getText().equals(p2.getText())){
            //DISPLAY MISMATCH PASSWORD WARNING
            Alert passMismatch = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "PASSWORD MISMATCH");
            passMismatch.show();
            key = false;
        }
        if(!validateLength(u,6,20)){
            //DISPLAY (USERNAME must be between 8-20 characters) WARNING (temporary req up to change)
            Alert userLength = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Username must be between 8-20 characters.");
            userLength.show();
            key = false;
        }
        if(!validateLength(p1,8,20)){
            //DISPLAY (PASSWORD must be between 8-20 characters) WARNING (temporary req up to change)
            Alert passLength = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Password must be between 8-20 characters.");
            passLength.show();
            key = false;
        }
        if(!Pattern.matches("^(?![,])(?!.*[,]{2})[a-zA-Z0-9._]+(?<![,])$", p1.getText())){
            //DO NOT USE COMMA FOR PASSWORD THX
            Alert noComma = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "Cannot use comma in password.");
            noComma.show();
            key = false;
        }
        if(!Pattern.matches("^(?![_.,])(?!.*[_.,]{2})[a-zA-Z0-9._]+(?<![_.,])$", u.getText())){
            //DISPLAY WRONG USERNAME FORMAT
            Alert userFormat = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "WRONG USERNAME FORMAT");
            userFormat.show();
            key = false;
        }
        if(!Pattern.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b", e.getText())){
            //DISPLAY WRONG EMAIL FORMAT
            Alert emailFormat = showAlert(Alert.AlertType.ERROR, pane.getScene().getWindow(), "Error!", "WRONG EMAIL FORMAT");
            emailFormat.show();
            key = false;
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

    Boolean validateUser(TextField u,File f) throws Exception{
        Map<Integer,String> users = getUsernames(f);
        String userName = u.getText();
        for (String names: users.values()){
            //System.out.println("Existing names:" + names);
            if(userName.equals(names)){
                return false;
            }
        }
        return true;
    }

    Boolean validateEmail(TextField e,File f) throws Exception{
        Map<Integer,String> users = getEmails(f);
        String email = e.getText();
        for (String names: users.values()){
            if(email.equals(names)){
                return false;
            }
        }
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
        input.close();
        return Integer.parseInt(words[0])+ 1;
    }

    String readAll(File f)throws Exception{
        //get all data, return as String[] comma splitted (csv)
        Scanner input = new Scanner(f);
        String line = "";
        int index = 0;
        while(input.hasNextLine()) {
            line += input.nextLine() + "\n";
        }
        return line;
    }

    Map<Integer,String> getUsernames(File f)throws Exception {
        //get all usernames for validation
        String data = readAll(f);
        //System.out.println("DATA:\n" + data);
        String[] splitted = data.split(",");
        Map<Integer,String> users = new HashMap<Integer,String>(); //unique id and username
        Scanner s = new Scanner(data).useDelimiter("\n");
        s.nextLine(); //skip header
        while(s.hasNextLine()){
            String[] words = s.nextLine().split(",");
            //System.out.println("ID:" + words[0] + "Name:" + words[2]);
            users.put(Integer.parseInt(words[0]),words[2]);
        }
        return users;
    }

    Map<Integer,String> getEmails(File f)throws Exception {
        //get all emails for validation too
        String data = readAll(f);
        String[] splitted = data.split(",");
        Map<Integer,String> users = new HashMap<Integer,String>(); //unique id and email
        Scanner s = new Scanner(data).useDelimiter("\n");
        s.nextLine(); //skip header
        while(s.hasNextLine()){
            String[] words = s.nextLine().split(",");
            users.put(Integer.parseInt(words[0]),words[1]);
        }
        return users;
    }

    private Alert showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        return alert;
    }

}

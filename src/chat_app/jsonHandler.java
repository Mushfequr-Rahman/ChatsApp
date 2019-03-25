package chat_app;

import chat_app.server.Message;
import chat_app.server.messagetype;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jsonHandler {
    //saved every single time a message is sent

    private String json; //json formatted log
    private String fileName; //'database for messages'

    jsonHandler(String f, String json){
        fileName = f;
        this.json = json;
    }

    jsonHandler(String f)
    {
        fileName = f;
        this.json = "";
    }

    String getInfoToWrite(){
        //parse json to get client,users,msg,type,time only

        Pattern p = Pattern.compile("(?<=:)(.*?)(,)");
        //match after : to , (Json altered to make life easier :D)
        Matcher matcher = p.matcher(json);
        String s = "";
        while (matcher.find()) {
            s += matcher.group();
        }
        return s;
    }

    void generateHeader() throws Exception{
        //file.exists() will be done on chatUI. If doesn't exist, trigger this.

        FileWriter file = new FileWriter(new File(fileName));
        file.write("Client,Users,Message,MessageType,TimeStamps\n");
        file.close();
    }

    void writeJson() throws Exception{
        //Write into CSV

        FileWriter file = new FileWriter(new File(fileName),true);
        //fix formatting
        String toWrite = getInfoToWrite().substring(0, getInfoToWrite().length() - 1); //last letter is currently a ','
        toWrite += "\n";
        file.write(toWrite);
        file.close();
    }

    String readFile(){
        Scanner input = new Scanner(fileName);
        String line = "";
        while(input.hasNextLine()) {
            line += input.nextLine() + "\n";
        }
        return line;
    }


    //NOT TESTED (need Add UserID)
    ArrayList<Message> filterCertainUsers(ArrayList<String> Users) throws Exception {
        //Return List of Message class of those with certain Users('chatroom')
        ArrayList<Message> m = new ArrayList<>();

        String data = chatUI.fileToString(fileName);
        Scanner s = new Scanner(data).useDelimiter("\n");
        s.nextLine(); //skip header
        Boolean key = true;
        while (s.hasNextLine()) {
            String[] datas = s.nextLine().split(",");
            for (String u : Users) {
                if (datas[1].contains(u)) {
                    key = false;
                }
            }
            if (key) {
                //add appropriate class
                Message mm = new Message(datas[0], Users, datas[3], datas[1]);
                //set message type
                mm.SetType(messagetype.valueOf(datas[4]));
                m.add(mm);
            }
        }
        return m;
    }
}

package chat_app.server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable
{
    public Message(String client, ArrayList<String> user, String message) {
        Client = client;
        Users = user;
        Message = message;
        TimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); //add timestamp

    }

    public String toJson()
    {
        String output = "{";
        output += String.format("Client:" + Client + ",");
        output += "Users: {";
        for(String Usr : Users)
        {
            output += String.format(Usr + ",") ;
        }
        output += "} , ";
        output += String.format("Message:" + Message + ",");
        output += String.format("TimeStamp:" + TimeStamp + "}");

        System.out.println(output);

        return output;
    }

    private String Client;
    private ArrayList<String> Users;
    private String Message;
    private String TimeStamp;




}

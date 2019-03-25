package chat_app.server;

import java.awt.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


//Serilazable Message class that is used store the Message format and convert to JSON for transmiting via Server Log

public class Message implements Serializable
{
    public Message(String client, ArrayList<String> user, String message) {
        Client = client;
        Users = user;
        Message = message;
        TimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); //add timestamp

    }
    public Message(String client, ArrayList<String> user, String message,String Session_ID) {
        Client = client;
        Users = user;
        Message = message;
        this.Session_ID = Session_ID;
        TimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); //add timestamp

    }

    public String toJson()
    {

        //JSON FORMAT: {Client: client, Session : Session ID, Users: {} , Message: message, MessageType: MessageType , TimestampL timestamp }
        String output = "{";
        output += String.format("Client:" + Client + ",");
        output += String.format("Session:" + Session_ID + ",");
        output += "Users: {";
        for(String Usr : Users)
        {
            output += String.format(Usr + ".") ;
        }
        output += "}, ";
        output += String.format("Message:" + Message + ",");
        output += String.format("MessageType:" + Type + ",");
        output += String.format("TimeStamp:" + TimeStamp + ", }");

        System.out.println(output);

        return output;
    }
    public void SetType(messagetype mt)
    {
        this.Type = mt;
    }

    public String getClientName(){return Client;}
    public String getMessage(){return Message;}
    public messagetype getType(){return this.Type;}
    public ArrayList<String> getUsers()
    {
        return this.Users;
    }

    private String Client;
    private String Session_ID;
    private ArrayList<String> Users;
    private String Message;
    private String TimeStamp;
    private messagetype Type = messagetype.TEXT;

    public String getSession_ID() {
        return Session_ID;
    }

    public void setSession_ID(String session_ID) {
        Session_ID = session_ID;
    }
}

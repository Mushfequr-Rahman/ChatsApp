package chat_app.server;

public class Server_test {


    private User create_user()
    {
        User user = new User();
        user.setName(String.format("user" + Math.random()*10));

        return user;

    }

    private String create_Message()
    {
        return String.format("Message" + Math.random());
    }


}

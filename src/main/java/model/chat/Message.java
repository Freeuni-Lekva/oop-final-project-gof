package model.chat;

public class Message {

    private boolean isUser;
    private String message;
    private int id;

    public Message(String message,boolean isUser, int message_id){
        this.message = message;
        this.isUser = isUser;
        this.id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public int getId() {
        return id;
    }

}

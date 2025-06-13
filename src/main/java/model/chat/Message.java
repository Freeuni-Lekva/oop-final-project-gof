package model.chat;

public class Message {

    private boolean isUser;
    private String message;
    private int id;
    private boolean isPrompt;

    public Message(String message,boolean isUser, int message_id, boolean isPrompt){
        this.message = message;
        this.isUser = isUser;
        this.id = message_id;
        this.isPrompt =  isPrompt;
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

    public boolean isPrompt() {
        return isPrompt;
    }

}

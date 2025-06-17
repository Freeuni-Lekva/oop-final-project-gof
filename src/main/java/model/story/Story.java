package model.story;

import java.time.LocalDateTime;

public class Story {
    private String title;
    private String prompt;
    private final int creatorId;
    private final int storyId;
    private final LocalDateTime creationDate;

    public Story(String title, String prompt, int creatorId, int storyId, LocalDateTime creationDate) {
        this.title = title;
        this.creatorId = creatorId;
        this.storyId = storyId;
        this.prompt = prompt;
        this.creationDate = creationDate;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getPrompt() {
        return prompt;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getStoryId() {
        return storyId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
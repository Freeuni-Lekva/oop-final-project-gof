package model.story;

import java.util.List;


public class PromptBuilder {

    private List<Character> characters;
    private String worldInfo;
    private String description = "";

    /**
     * Constructs a new PromptBuilder with the given characters and world information.
     *
     * @param characters List of Character objects to include in the prompt.
     * @param worldInfo  Description of the world/setting for the story.
     */
    public PromptBuilder(List<Character> characters, String worldInfo, String description) {
        this.characters = characters;
        this.worldInfo = worldInfo;
        this.description = description;
    }


    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public String getWorldInfo() {
        return worldInfo;
    }

    public void setWorldInfo(String worldInfo) {
        this.worldInfo = worldInfo;
    }

    /**
     * Builds a combined prompt string containing world information followed by
     * the formatted details of each character. This string can be sent as input
     * to an AI model like Gemini.
     *
     * @return A formatted prompt ready for submission to the AI.
     */
    public String build() {
        StringBuilder prompt = new StringBuilder();

        prompt.append("World Information:\n")
                .append(worldInfo)
                .append("\n\n");

        prompt.append("Story description:\n")
                .append(description)
                .append("\n\n");

        prompt.append("Characters:\n");
        for (Character character : characters) {
            prompt.append("- ")
                    .append(character.getName())
                    .append(" (Age: ")
                    .append(character.getAge())
                    .append(", Gender: ")
                    .append(character.getGender())
                    .append(", Species: ")
                    .append(character.getSpecies())
                    .append("): ")
                    .append(character.getDescription())
                    .append("\n");
        }

        prompt.append("\n\n");

        prompt.append("Now, begin the story.\n")
                .append("DO NOT list the description, world info, or characters. DO NOT say 'Welcome' or 'Here is the story'.\n")
                .append("Instead, write an engaging and immersive opening paragraph that immediately drops the player into the scene.\n")
                .append("Your opening message should set the tone and atmosphere.\n")
                .append("End your opening message with a clear question to the player, asking them what they want to do next.\n");

        return prompt.toString();
    }

}

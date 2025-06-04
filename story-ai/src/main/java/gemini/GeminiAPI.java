package gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiAPI {

    private final Client geminiClient;
    private static final String DEFAULT_MODEL = "gemini-2.0-flash";

    public GeminiAPI() {
        this.geminiClient = new Client();
    }


    public String generateContent(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty.");
        }

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        DEFAULT_MODEL,
                        prompt,
                        null
                );

        if (response != null && response.text() != null && !response.text().isEmpty()) {
            return response.text();
        } else {
            System.err.println("Gemini API returned no text for prompt: " + prompt);
            throw new RuntimeException("Failed to generate content from Gemini API.");
        }
    }

}

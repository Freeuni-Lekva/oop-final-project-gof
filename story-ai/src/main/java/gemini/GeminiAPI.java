package gemini;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

public class GeminiAPI {

    private final Client geminiClient;
    private static final String DEFAULT_MODEL = "gemini-1.5-flash";


    public GeminiAPI() {
        this.geminiClient = new Client();
    }


    public String generateContent(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty.");
        }

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(
                        Part.fromText(Prompts.SYSTEM_PROMPT)))
                .build();

        Content userContent = Content.fromParts(Part.fromText(prompt));

        GenerateContentResponse response =
                geminiClient.models.generateContent(DEFAULT_MODEL, userContent, config);


        if (response != null && response.text() != null && !response.text().isEmpty()) {
            return response.text();
        } else {
            System.err.println("Gemini API returned no text for prompt: " + prompt);
            throw new RuntimeException("Failed to generate content from Gemini API.");
        }
    }


}

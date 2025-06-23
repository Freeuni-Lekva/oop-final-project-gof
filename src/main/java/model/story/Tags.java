package model.story;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class Tags {

    private static final List<String> TAGS = Collections.unmodifiableList(Arrays.asList(
            "Adventure",
            "Mystery",
            "Technology",
            "Sci-Fi",
            "Detective",
            "Drama"
    ));

    private static final List<String> TAGS_TO_LOWER = Collections.unmodifiableList(Arrays.asList(
            "adventure",
            "mystery",
            "technology",
            "sci-Fi",
            "detective",
            "drama"
    ));

    public static List<String> getAllTags() {
        return TAGS;
    }

    public static boolean isValidTag(String tag) {
        return TAGS.contains(tag) || TAGS_TO_LOWER.contains(tag.toLowerCase());
    }
}

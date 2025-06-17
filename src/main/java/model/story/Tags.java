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

    public static List<String> getAllTags() {
        return TAGS;
    }

    public static boolean isValidTag(String tag) {
        return TAGS.contains(tag);
    }
}

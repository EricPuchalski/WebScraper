package WebScraper.utils;

import WebScraper.constant.CategoriesMessages;

import java.util.List;
import java.util.stream.Collectors;

public class FullH4rdUtils {

    public static List<String> getAllCategoryUrls() {
        return CategoriesMessages.FULLH4RD_CATEGORIES.stream()
                .map(cat -> CategoriesMessages.FULLH4RD_BASE_URL + cat)
                .collect(Collectors.toList());
    }

}
package WebScraper.utils;

import WebScraper.constant.CategoriesMessages;

import java.util.List;
import java.util.stream.Collectors;

public class GezatekUtils {

    public static List<String> getAllCategoryUrls() {
        return CategoriesMessages.GEZATEK_CATEGORIES.stream()
                .map(cat -> CategoriesMessages.GEZATEK_BASE_URL + cat)
                .collect(Collectors.toList());
    }

}

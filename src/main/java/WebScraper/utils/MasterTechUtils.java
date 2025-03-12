package WebScraper.utils;

import java.util.ArrayList;
import java.util.List;

public class MasterTechUtils {
    private static final String BASE_URL = "https://www.mastertech.com.py/categoria-producto/";

    /**
     * Obtiene la lista completa de URLs de categorías
     * @return Lista de URLs de categorías
     */
    public static List<String> getAllCategoryUrls() {
        List<String> urls = new ArrayList<>();

        // Notebooks
        urls.add(BASE_URL + "notebooks/");

        return urls;
    }

    /**
     * Obtiene URLs de una categoría específica
     * @param category Nombre de la categoría principal
     * @return Lista de URLs que pertenecen a esa categoría
     */
    public static List<String> getCategoryUrlsByType(String category) {
        List<String> allUrls = getAllCategoryUrls();
        List<String> filteredUrls = new ArrayList<>();

        for (String url : allUrls) {
            if (url.contains(category.toLowerCase())) {
                filteredUrls.add(url);
            }
        }

        return filteredUrls;
    }
}

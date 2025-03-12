package WebScraper.utils;

import java.util.ArrayList;
import java.util.List;

public class FullH4rdUtils {
    private static final String BASE_URL = "https://fullh4rd.com.ar/cat/supra/";

    /**
     * Obtiene la lista completa de URLs de categorías con página 1
     * @return Lista de URLs de categorías
     */
    public static List<String> getAllCategoryUrls() {
        List<String> urls = new ArrayList<>();

        // Categorías FullH4rd
        urls.add(BASE_URL + "54/accesorios/1");
        urls.add(BASE_URL + "12/almacenamiento/1");
        urls.add(BASE_URL + "28/auriculares-y-microfonos/1");
        urls.add(BASE_URL + "52/cables/1");
        urls.add(BASE_URL + "21/camaras/1");
        urls.add(BASE_URL + "27/conectividad-y-redes/1");
        urls.add(BASE_URL + "48/consolas/1");
        urls.add(BASE_URL + "5/equipos/1");
        urls.add(BASE_URL + "25/estabilizadores-y-ups/1");
        urls.add(BASE_URL + "26/fuentes/1");
        urls.add(BASE_URL + "6/gabinetes/1");
        urls.add(BASE_URL + "20/impresoras/1");
        urls.add(BASE_URL + "44/joystick/1");
        urls.add(BASE_URL + "4/memorias/1");
        urls.add(BASE_URL + "1/microprocesador/1");
        urls.add(BASE_URL + "18/monitores/1");
        urls.add(BASE_URL + "2/motherboard/1");
        urls.add(BASE_URL + "56/mouse-pad/1");
        urls.add(BASE_URL + "14/mouses/1");
        urls.add(BASE_URL + "30/multimedia/1");
        urls.add(BASE_URL + "32/notebooks/1");
        urls.add(BASE_URL + "15/parlantes/1");
        urls.add(BASE_URL + "33/pendrives/1");
        urls.add(BASE_URL + "3/placas-de-video/1");
        urls.add(BASE_URL + "37/proyectores/1");
        urls.add(BASE_URL + "23/refrigeracion/1");
        urls.add(BASE_URL + "55/sillas-gamer/1");
        urls.add(BASE_URL + "60/smartwatches/1");
        urls.add(BASE_URL + "34/software/1");
        urls.add(BASE_URL + "8/teclados/1");
        urls.add(BASE_URL + "31/telefonia/1");

        return urls;
    }

    /**
     * Obtiene URLs de una categoría específica
     * @param categoryId ID de la categoría
     * @return Lista de URLs que pertenecen a esa categoría (solo página 1)
     */
    public static List<String> getCategoryUrlsById(String categoryId) {
        List<String> allUrls = getAllCategoryUrls();
        List<String> filteredUrls = new ArrayList<>();

        for (String url : allUrls) {
            if (url.contains("/" + categoryId + "/")) {
                filteredUrls.add(url);
            }
        }

        return filteredUrls;
    }

    /**
     * Obtiene URLs de una categoría específica por nombre
     * @param categoryName Nombre de la categoría
     * @return Lista de URLs que pertenecen a esa categoría (solo página 1)
     */
    public static List<String> getCategoryUrlsByName(String categoryName) {
        List<String> allUrls = getAllCategoryUrls();
        List<String> filteredUrls = new ArrayList<>();

        for (String url : allUrls) {
            if (url.toLowerCase().contains(categoryName.toLowerCase())) {
                filteredUrls.add(url);
            }
        }

        return filteredUrls;
    }

    /**
     * Obtiene URLs solo para componentes de PC
     * @return Lista de URLs de categorías de componentes
     */
    public static List<String> getComponentsUrls() {
        List<String> componentsUrls = new ArrayList<>();

        componentsUrls.addAll(getCategoryUrlsByName("microprocesador"));
        componentsUrls.addAll(getCategoryUrlsByName("motherboard"));
        componentsUrls.addAll(getCategoryUrlsByName("memorias"));
        componentsUrls.addAll(getCategoryUrlsByName("placas-de-video"));
        componentsUrls.addAll(getCategoryUrlsByName("almacenamiento"));
        componentsUrls.addAll(getCategoryUrlsByName("fuentes"));
        componentsUrls.addAll(getCategoryUrlsByName("gabinetes"));
        componentsUrls.addAll(getCategoryUrlsByName("refrigeracion"));

        return componentsUrls;
    }

    /**
     * Obtiene URLs solo para periféricos
     * @return Lista de URLs de categorías de periféricos
     */
    public static List<String> getPeripheralsUrls() {
        List<String> peripheralsUrls = new ArrayList<>();

        peripheralsUrls.addAll(getCategoryUrlsByName("mouses"));
        peripheralsUrls.addAll(getCategoryUrlsByName("teclados"));
        peripheralsUrls.addAll(getCategoryUrlsByName("auriculares"));
        peripheralsUrls.addAll(getCategoryUrlsByName("parlantes"));
        peripheralsUrls.addAll(getCategoryUrlsByName("camaras"));
        peripheralsUrls.addAll(getCategoryUrlsByName("joystick"));
        peripheralsUrls.addAll(getCategoryUrlsByName("monitores"));

        return peripheralsUrls;
    }
}
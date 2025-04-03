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

}
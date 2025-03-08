package WebScraper.utils;

import java.util.ArrayList;
import java.util.List;

public class GezatekUtils {
    private static final String BASE_URL = "https://www.gezatek.com.ar/tienda/";

    /**
     * Obtiene la lista completa de URLs de categorías
     * @return Lista de URLs de categorías
     */
    public static List<String> getAllCategoryUrls() {
        List<String> urls = new ArrayList<>();

        // PC y Notebooks
        urls.add(BASE_URL + "pc-hogar-y-oficina/");
        urls.add(BASE_URL + "pc-basic-gamer/");
        urls.add(BASE_URL + "pc-elite-gamer/");
        urls.add(BASE_URL + "pc-xtreme-gamer/");
        urls.add(BASE_URL + "notebooks/");
        urls.add(BASE_URL + "accesorios-notebook/");

        // Electrónica de consumo
        urls.add(BASE_URL + "smart-tv/");
        urls.add(BASE_URL + "celulares/");
        urls.add(BASE_URL + "tablets/");

        // Componentes AMD
        urls.add(BASE_URL + "combos-amd/");
        urls.add(BASE_URL + "procesadores-amd/");
        urls.add(BASE_URL + "mothers-amd-am4/");
        urls.add(BASE_URL + "mothers-amd-am5/");

        // Componentes Intel
        urls.add(BASE_URL + "combos-intel/");
        urls.add(BASE_URL + "procesadores-intel/");
        urls.add(BASE_URL + "mothers-intel-lga1200/");
        urls.add(BASE_URL + "mothers-intel-lga1700/");
        urls.add(BASE_URL + "mothers-intel-lga1851/");

        // Memoria RAM
        urls.add(BASE_URL + "ram-ddr3/");
        urls.add(BASE_URL + "ram-ddr4/");
        urls.add(BASE_URL + "ram-ddr5/");
        urls.add(BASE_URL + "ram-sodimm-ddr4/");

        // Tarjetas gráficas y monitores
        urls.add(BASE_URL + "vga-nvidia/");
        urls.add(BASE_URL + "vga-amd/");
        urls.add(BASE_URL + "monitores/");

        // Almacenamiento
        urls.add(BASE_URL + "discos-ssd-sata3/");
        urls.add(BASE_URL + "discos-ssd-m2-nvme/");
        urls.add(BASE_URL + "discos-rigidos/");
        urls.add(BASE_URL + "discos-externos-usb/");
        urls.add(BASE_URL + "opticos/");

        // Alimentación y gabinetes
        urls.add(BASE_URL + "fuentes/");
        urls.add(BASE_URL + "gabinetes/");
        urls.add(BASE_URL + "gabinetes-con-fuente/");

        // Periféricos
        urls.add(BASE_URL + "mouse/");
        urls.add(BASE_URL + "mousepads/");
        urls.add(BASE_URL + "teclados-kit/");
        urls.add(BASE_URL + "auriculares/");
        urls.add(BASE_URL + "parlantes/");
        urls.add(BASE_URL + "webcam/");
        urls.add(BASE_URL + "gamepad-joystick/");
        urls.add(BASE_URL + "impresoras/");

        // Accesorios y otros
        urls.add(BASE_URL + "estabilizadores/");
        urls.add(BASE_URL + "sillas/");
        urls.add(BASE_URL + "coolers-cpu/");
        urls.add(BASE_URL + "water-cooling/");
        urls.add(BASE_URL + "cooler-fan/");

        // Redes
        urls.add(BASE_URL + "routers-y-repetidores-wifi/");
        urls.add(BASE_URL + "placas-de-red-usb/");
        urls.add(BASE_URL + "placas-de-red-internas/");
        urls.add(BASE_URL + "switches-de-red/");

        // Otros periféricos y accesorios
        urls.add(BASE_URL + "tabletas-digitalizadoras/");
        urls.add(BASE_URL + "micro-sd/");
        urls.add(BASE_URL + "pendrive/");
        urls.add(BASE_URL + "adaptadores/");
        urls.add(BASE_URL + "cables/");
        urls.add(BASE_URL + "insumos/");
        urls.add(BASE_URL + "streaming/");
        urls.add(BASE_URL + "servicios/");

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

    /**
     * Obtiene URLs solo para componentes de PC
     * @return Lista de URLs de categorías de componentes
     */
    public static List<String> getComponentsUrls() {
        List<String> componentsUrls = new ArrayList<>();

        componentsUrls.addAll(getCategoryUrlsByType("procesadores"));
        componentsUrls.addAll(getCategoryUrlsByType("mothers"));
        componentsUrls.addAll(getCategoryUrlsByType("ram"));
        componentsUrls.addAll(getCategoryUrlsByType("vga"));
        componentsUrls.addAll(getCategoryUrlsByType("discos"));
        componentsUrls.addAll(getCategoryUrlsByType("fuentes"));
        componentsUrls.addAll(getCategoryUrlsByType("gabinetes"));
        componentsUrls.addAll(getCategoryUrlsByType("coolers"));

        return componentsUrls;
    }

    /**
     * Obtiene URLs solo para periféricos
     * @return Lista de URLs de categorías de periféricos
     */
    public static List<String> getPeripheralsUrls() {
        List<String> peripheralsUrls = new ArrayList<>();

        peripheralsUrls.addAll(getCategoryUrlsByType("mouse"));
        peripheralsUrls.addAll(getCategoryUrlsByType("teclados"));
        peripheralsUrls.addAll(getCategoryUrlsByType("auriculares"));
        peripheralsUrls.addAll(getCategoryUrlsByType("parlantes"));
        peripheralsUrls.addAll(getCategoryUrlsByType("webcam"));
        peripheralsUrls.addAll(getCategoryUrlsByType("gamepad"));
        peripheralsUrls.addAll(getCategoryUrlsByType("monitors"));

        return peripheralsUrls;
    }
}

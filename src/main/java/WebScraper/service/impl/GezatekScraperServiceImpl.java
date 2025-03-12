package WebScraper.service.impl;

import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.GezatekScraperRepository;
import WebScraper.service.GezatekScraperService;
import WebScraper.utils.GezatekUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GezatekScraperServiceImpl implements GezatekScraperService {
    private static final Logger logger = Logger.getLogger(GezatekScraperServiceImpl.class.getName());
    private final GezatekScraperRepository gezatekScraperRepository;
    private final String baseUrl = "https://www.gezatek.com.ar";
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";


    public GezatekScraperServiceImpl(GezatekScraperRepository gezatekScraperRepository) {
        this.gezatekScraperRepository = gezatekScraperRepository;
    }

    @Override
    public List<Product> updateProducts() {
        List<Product> productList = new ArrayList<>();
        List<String> categories = GezatekUtils.getAllCategoryUrls();

        for (String url : categories) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .timeout(10000)
                        .get();
                Elements products = doc.select(".product");

                for (Element product : products) {
                    String title = product.select("h2").text();
                    String priceText = product.select("div > h3").text().substring(12);
                    Double price = Double.valueOf(priceText);
                    String imageUrl = product.select(".img-responsive").attr("src");
                    String relativeProductUrl = product.select(".click").attr("href");
                    String productUrl = baseUrl + relativeProductUrl;
                    String page = "Gezatek";

                    // Verificar si el producto ya existe en la base de datos
                    Optional<Product> existingProduct = gezatekScraperRepository.findByProductUrl(productUrl);

                    if (existingProduct.isPresent()) {
                        Product productToUpdate = existingProduct.get();
                        productToUpdate.setName(title);
                        productToUpdate.setImageUrl(imageUrl);
                        productToUpdate.setPage(page);

                        // Verificar si el precio ha cambiado
                        List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
                        if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
                            productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now())); // Agregar nuevo precio al historial
                        }

                        gezatekScraperRepository.save(productToUpdate);
                        productList.add(productToUpdate);
                    } else {
                        // Insertar nuevo producto si no existe
                        Product newProduct = new Product(title, imageUrl, productUrl, page);
                        newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now()));
                        gezatekScraperRepository.save(newProduct);
                        productList.add(newProduct);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en URL " + url, e);
            }
        }
        return productList;
    }


    @Override
    public List<Product> findProducts(String name) {
        return gezatekScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && "gezatek".equalsIgnoreCase(product.getPage()))
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return gezatekScraperRepository.findAll().stream()
                .filter(product -> "gezatek".equalsIgnoreCase(product.getPage()))
                .toList();
    }

    // REVISAR TODO ESTE CODIGO
//    @Override
//    public List<Product> findProducts(String name) {
//        if (name == null || name.isEmpty()) {
//            return gezatekScraperRepository.findAll();
//        }
//
//        List<Product> results = new ArrayList<>();
//        String searchTerm = name.toLowerCase();
//
//        for (Product product : gezatekScraperRepository.findAll()) {
//            if (product.getName().toLowerCase().contains(searchTerm)) {
//                results.add(product);
//            }
//        }
//
//        return results;
//    }
//
//    /**
//     * Método para actualizar productos solo de una categoría específica
//     * @param category Nombre de la categoría
//     * @return Lista de productos de esa categoría
//     */
//    public List<Product> updateProductsByCategory(String category) {
//        List<Product> productList = new ArrayList<>();
//        List<String> categoryUrls = GezatekUtils.getCategoryUrlsByType(category);
//
//        for (String url : categoryUrls) {
//            try {
//                Document doc = Jsoup.connect(url).get();
//                Elements products = doc.select(".product");
//
//                for (Element product : products) {
//                    if (!product.hasClass("sin-stock") && !product.html().contains("Sin Stock")) {
//                        String title = product.select("h2").text();
//                        String price = product.select("div > h3").text().substring(12);
//                        String imageUrl = product.select(".img-responsive").attr("src");
//                        String relativeProductUrl = product.select(".click").attr("href");
//                        String productUrl = new URL(new URL(baseUrl), relativeProductUrl).toString();
//
//                        Product newProduct = new Product(title, Double.valueOf(price), imageUrl, productUrl);
//                        productList.add(newProduct);
//                        gezatekScraperRepository.save(newProduct);
//                    }
//                }
//            } catch (Exception e) {
//                logger.log(Level.SEVERE, "Error en URL " + url, e);
//            }
//        }
//        return productList;
//    }
}

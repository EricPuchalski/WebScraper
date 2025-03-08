package WebScraper.service.impl;

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
import java.util.ArrayList;
import java.util.List;

@Service
public class GezatekScraperServiceImpl implements GezatekScraperService {
    private final GezatekScraperRepository gezatekScraperRepository;
    private final String baseUrl = "https://www.gezatek.com.ar";

    public GezatekScraperServiceImpl(GezatekScraperRepository gezatekScraperRepository) {
        this.gezatekScraperRepository = gezatekScraperRepository;
    }

    @Override
    public List<Product> updateProducts() {
        List<Product> productList = new ArrayList<>();

        List<String> categories = GezatekUtils.getAllCategoryUrls();

        for (String url : categories) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements products = doc.select(".product");

                for (Element product : products) {
                    String title = product.select("h2").text();
                    String price = product.select("div > h3").text().substring(12);
                    String imageUrl = product.select(".img-responsive").attr("src");
                    String relativeProductUrl = product.select(".click").attr("href");
                    String productUrl = new URL(new URL(baseUrl), relativeProductUrl).toString();

                    Product newProduct = new Product(title, Double.valueOf(price), imageUrl, productUrl);
                    productList.add(newProduct);
                    gezatekScraperRepository.save(newProduct);
                }
            } catch (Exception e) {
                System.out.println("Error en URL " + url + ": " + e.getMessage());
            }
        }
        return productList;
    }
    @Override
   public List<Product> findProducts(String name) {
        return null;
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
//                System.out.println("Error en URL " + url + ": " + e.getMessage());
//            }
//        }
//        return productList;
//    }
}
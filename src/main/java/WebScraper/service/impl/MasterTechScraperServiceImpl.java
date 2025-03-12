package WebScraper.service.impl;

import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.MasterTechScraperRepository;
import WebScraper.service.MasterTechScraperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MasterTechScraperServiceImpl implements MasterTechScraperService {
    private static final Logger logger = Logger.getLogger(MasterTechScraperServiceImpl.class.getName());
    private final MasterTechScraperRepository masterTechScraperRepository;
    private final String baseUrl = "https://www.mastertech.com.py/productos";
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    public MasterTechScraperServiceImpl(MasterTechScraperRepository masterTechScraperRepository) {
        this.masterTechScraperRepository = masterTechScraperRepository;
    }

    @Override
    public List<Product> updateProducts() {
        List<Product> productList = new ArrayList<>();
        int page = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            try {
                String url = page == 1 ? baseUrl + "/" : baseUrl + "/page/" + page + "/";
                logger.info("Procesando URL: " + url);

                Document doc = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .timeout(10000)
                        .get();

                Elements products = doc.select(".product");
                if (products.isEmpty()) {
                    hasNextPage = false;
                    continue;
                }

                for (Element product : products) {
                    try {
                        String title = product.select(".ast-loop-product__link").text();

                        Element priceElement = product.select(".price span bdi").first();
                        String priceText = priceElement != null ? priceElement.ownText().replaceAll("[^\\d]", "") : "0";
                        Double price = Double.valueOf(priceText);

                        String imgUrl = product.select("noscript > img").attr("src");
                        String productUrl = product.select(".ast-loop-product__link").attr("href");
                        String pageName = "MasterTech";

                        Optional<Product> existingProduct = masterTechScraperRepository.findByProductUrl(productUrl);
                        if (existingProduct.isPresent()) {
                            Product productToUpdate = existingProduct.get();
                            productToUpdate.setName(title);
                            productToUpdate.setImageUrl(imgUrl);
                            productToUpdate.setPage(pageName);

                            List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
                            if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
                                productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
                            }
                            masterTechScraperRepository.save(productToUpdate);
                            productList.add(productToUpdate);
                        } else {
                            Product newProduct = new Product(title, imgUrl, productUrl, pageName);
                            newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "PYG"));
                            masterTechScraperRepository.save(newProduct);
                            productList.add(newProduct);
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
                    }
                }

                Element nextPageLink = doc.selectFirst("a.next");
                if (nextPageLink != null) {
                    page++;
                } else {
                    hasNextPage = false;
                }

//                Thread.sleep(500); POR SI ME TIRA ERROR DE SOBRECARGA DE PETICIONES EN LA PAGINA
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en la paginación: " + page, e);
                hasNextPage = false;
            }
        }
        return productList;
    }

    @Override
    public List<Product> findProducts(String name) {
        return masterTechScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && "MasterTech".equalsIgnoreCase(product.getPage()))
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return masterTechScraperRepository.findAll().stream()
                .filter(product -> "MasterTech".equalsIgnoreCase(product.getPage()))
                .toList();
    }
}

package WebScraper.service.impl;

import WebScraper.model.PriceHistory;
import WebScraper.model.Product;
import WebScraper.repository.FullH4rdScraperRepository;
import WebScraper.service.FullH4rdScraperService;
import WebScraper.utils.FullH4rdUtils;
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
public class FullH4rdScraperServiceImpl implements FullH4rdScraperService {
    private static final Logger logger = Logger.getLogger(FullH4rdScraperServiceImpl.class.getName());
    private final FullH4rdScraperRepository fullH4rdScraperRepository;
    private final String baseUrl = "https://fullh4rd.com.ar";
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    public FullH4rdScraperServiceImpl(FullH4rdScraperRepository fullH4rdScraperRepository) {
        this.fullH4rdScraperRepository = fullH4rdScraperRepository;
    }

    @Override
    public List<Product> updateProducts() {
        List<Product> productList = new ArrayList<>();
        List<String> categories = FullH4rdUtils.getAllCategoryUrls();

        for (String categoryUrl : categories) {
            try {
                int page = 1;
                boolean hasNextPage = true;

                while (hasNextPage) {
                    // Construir la URL para la página actual
                    String url = categoryUrl.replaceAll("/\\d+$", "/" + page);
                    logger.info("Procesando URL: " + url);

                    // Conectar a la URL con el userAgent configurado
                    Document doc = Jsoup.connect(url)
                            .userAgent(userAgent)
                            .timeout(10000)
                            .get();

                    Elements products = doc.select(".product-list");

                    // Si no hay productos, salimos del ciclo
                    if (products.isEmpty()) {
                        hasNextPage = false;
                        continue;
                    }

                    for (Element product : products) {
                        try {
                            String title = product.select(".info > h3").text();

                            Element priceElement = product.select(".price").first();
                            // Obtener solo el texto directo del div, excluyendo el contenido del span
                            String priceText = priceElement.ownText();
                            if (priceText.contains(",")) {
                                priceText = priceText.split(",")[0];
                            }
                            priceText = priceText.replaceAll("[^\\d]", "");
                            Double price = Double.valueOf(priceText);

                            // Obtener la URL de la imagen
                            String relativeImageUrl = product.select("a > div > img").attr("src");
                            String imageUrl = baseUrl + (relativeImageUrl.startsWith("/") ? "" : "/") + relativeImageUrl;

                            // Obtener la URL del producto
                            String relativeProductUrl = product.select("a").attr("href");
                            String productUrl = baseUrl + (relativeProductUrl.startsWith("/") ? "" : "/") + relativeProductUrl;

                            String page_name = "FullH4rd";

                            // Verificar si el producto ya existe en la base de datos
                            Optional<Product> existingProduct = fullH4rdScraperRepository.findByProductUrl(productUrl);

                            if (existingProduct.isPresent()) {
                                Product productToUpdate = existingProduct.get();
                                productToUpdate.setName(title);
                                productToUpdate.setImageUrl(imageUrl);
                                productToUpdate.setPage(page_name);

                                // Verificar si el precio ha cambiado
                                List<PriceHistory> priceHistory = productToUpdate.getPriceHistory();
                                if (priceHistory.isEmpty() || !price.equals(priceHistory.get(priceHistory.size() - 1).getPrice())) {
                                    productToUpdate.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS")); // Agregar nuevo precio al historial
                                }

                                fullH4rdScraperRepository.save(productToUpdate);
                                productList.add(productToUpdate);
                            } else {
                                // Insertar nuevo producto si no existe
                                Product newProduct = new Product(title, imageUrl, productUrl, page_name);
                                newProduct.getPriceHistory().add(new PriceHistory(price, LocalDateTime.now(), "ARS"));
                                fullH4rdScraperRepository.save(newProduct);
                                productList.add(newProduct);
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Error procesando producto individual: " + e.getMessage(), e);
                            // Continuar con el siguiente producto
                        }
                    }

                    // Verificar si hay página siguiente
                    Element nextPageLink = doc.selectFirst("a[rel=next]");
                    if (nextPageLink != null) {
                        page++;
                    } else {
                        hasNextPage = false;
                    }

                    // ACTIVAR un pequeño retraso para no sobrecargar el servidor si hace falta
//                    Thread.sleep(1500);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en URL " + categoryUrl, e);
            }
        }
        return productList;
    }


    @Override
    public List<Product> findProducts(String name) {
        // Filtrar productos por nombre y página "fullh4rd"
        return fullH4rdScraperRepository.findAll().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase())
                        && "fullh4rd".equalsIgnoreCase(product.getPage()))
                .toList();
    }

    @Override
    public List<Product> findAll() {
        return fullH4rdScraperRepository.findAll().stream()
                .filter(product -> "fullh4rd".equalsIgnoreCase(product.getPage()))
                .toList();
    }
}

package WebScraper;

import WebScraper.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WebScraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebScraperApplication.class, args);
		String url = "https://fullh4rd.com.ar/cat";
		String baseUrl = "https://fullh4rd.com.ar"; // URL base para construir URLs completas

		try {
			System.out.println("\nScrapeando: " + url);

			// Conectarse a la URL y obtener el documento HTML
			Document doc = Jsoup.connect(url).get();

			// Seleccionar los productos
			Elements products = doc.select(".product-list"); // Ajusta el selector según la estructura HTML de la página

			// Iterar sobre cada producto encontrado
			for (Element product : products) {
				String title = product.select(".info > h3").text(); // Ajusta el selector según la estructura HTML
				String priceText = product.select(".price-promo").text(); // Ajusta el selector según la estructura HTML

				// Obtener la URL de la imagen
				String relativeImageUrl = product.select("a > div > img").attr("src"); // Ajusta el selector según la estructura HTML
				String imageUrl = new URL(new URL(baseUrl), relativeImageUrl).toString(); // Construir la URL completa

				// Obtener la URL del producto
				String relativeProductUrl = product.select("a").attr("href"); // Ajusta el selector según la estructura HTML
				String productUrl = new URL(new URL(baseUrl), relativeProductUrl).toString(); // Construir la URL completa

				// Mostrar el producto en la consola
				System.out.println("Producto: " + title + " - Precio: " + priceText + " - Imagen: " + imageUrl + " - URL del Producto: " + productUrl);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Error al convertir el precio a número: " + e.getMessage());
		}
	}
}

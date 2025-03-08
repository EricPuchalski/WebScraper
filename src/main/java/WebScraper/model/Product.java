package WebScraper.model;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "products")
public class Product {
    private String name;

    private Double price;

    private String imageUrl;

    private String productUrl;

    public Product(String name, Double price, String urlImage, String urlProduct) {
        this.name = name;
        this.price = price;
        this.imageUrl = urlImage;
        this.productUrl = urlProduct;
    }


    public Product() {
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}

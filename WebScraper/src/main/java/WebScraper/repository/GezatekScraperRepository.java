package WebScraper.repository;

import WebScraper.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GezatekScraperRepository extends ScraperRepository{
    List<Product> findAllByPageAndActiveTrue(String page);

}

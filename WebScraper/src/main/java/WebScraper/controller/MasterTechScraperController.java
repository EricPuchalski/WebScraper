package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.service.MasterTechScraperService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraper/master-tech")
public class MasterTechScraperController {
    private final MasterTechScraperService masterTechScraperService;

    public MasterTechScraperController(MasterTechScraperService masterTechScraperService) {
        this.masterTechScraperService = masterTechScraperService;
    }

    @PostMapping()
    public ResponseEntity<List<ProductResponseDto>> updateProducts() {
        masterTechScraperService.updateProducts();
        return ResponseEntity.ok().body(null);
    }

}

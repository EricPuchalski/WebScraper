package WebScraper.controller;

import WebScraper.dto.ProductResponseDto;
import WebScraper.model.Product;
import WebScraper.service.FullH4rdScraperService;
import WebScraper.service.GezatekScraperService;
import WebScraper.service.MasterTechScraperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/scraper/all")
public class AdminController {
    private final FullH4rdScraperService fullH4rdScraperService;
    private final GezatekScraperService gezatekScraperService;
    private final MasterTechScraperService masterTechScraperService;

    public AdminController(FullH4rdScraperService fullH4rdScraperService, GezatekScraperService gezatekScraperService, MasterTechScraperService masterTechScraperService) {
        this.fullH4rdScraperService = fullH4rdScraperService;
        this.gezatekScraperService = gezatekScraperService;
        this.masterTechScraperService = masterTechScraperService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> findAllCombined(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {

        // Crear un objeto Pageable con los parámetros de página y tamaño
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todos los productos de cada tienda sin paginación
        List<ProductResponseDto> fullH4rdProducts = fullH4rdScraperService.findAll(0, Integer.MAX_VALUE).getContent();
        List<ProductResponseDto> gezatekProducts = gezatekScraperService.findAll(0, Integer.MAX_VALUE).getContent();
        List<ProductResponseDto> masterTechProducts = masterTechScraperService.findAll(0, Integer.MAX_VALUE).getContent();

        // Combinar todos los productos de las tres listas
        List<ProductResponseDto> combinedProducts = Stream.of(
                        fullH4rdProducts,
                        gezatekProducts,
                        masterTechProducts
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Aplicar la paginación sobre la lista combinada
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), combinedProducts.size());

        List<ProductResponseDto> paginatedProducts = (fromIndex > combinedProducts.size())
                ? List.of()
                : combinedProducts.subList(fromIndex, toIndex);

        // Crear una nueva página combinada con los productos paginados
        Page<ProductResponseDto> combinedPage = new PageImpl<>(paginatedProducts, pageable, combinedProducts.size());

        // Devolver los productos combinados como una respuesta paginada
        return ResponseEntity.ok(combinedPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size) {

        // Crear un objeto Pageable con los parámetros de página y tamaño
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todos los productos que coinciden con el nombre sin paginación
        List<ProductResponseDto> fullH4rdProducts = fullH4rdScraperService.findProducts(name, 0, Integer.MAX_VALUE).getContent();
        List<ProductResponseDto> gezatekProducts = gezatekScraperService.findProducts(name, 0, Integer.MAX_VALUE).getContent();
        List<ProductResponseDto> masterTechProducts = masterTechScraperService.findProducts(name, 0, Integer.MAX_VALUE).getContent();

        // Combinar todos los productos de las tres listas
        List<ProductResponseDto> combinedProducts = Stream.of(
                        fullH4rdProducts,
                        gezatekProducts,
                        masterTechProducts
                )
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Aplicar la paginación sobre la lista combinada
        int fromIndex = (int) pageable.getOffset();
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), combinedProducts.size());

        List<ProductResponseDto> paginatedProducts = (fromIndex > combinedProducts.size())
                ? List.of()
                : combinedProducts.subList(fromIndex, toIndex);

        // Crear una nueva página combinada con los productos paginados
        Page<ProductResponseDto> combinedPage = new PageImpl<>(paginatedProducts, pageable, combinedProducts.size());

        // Devolver los productos combinados como una respuesta paginada
        return ResponseEntity.ok(combinedPage);
    }
}

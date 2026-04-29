package WebScraper.constant;

public class CssSelectorsMessages {

    // ================= FULLH4RD =================
    public static final String FULLH4RD_PRODUCT_LIST   = ".product-list";
    public static final String FULLH4RD_TITLE          = ".info > h3";
    public static final String FULLH4RD_PRICE          = ".price";
    public static final String FULLH4RD_IMAGE          = "a > div > img";
    public static final String FULLH4RD_LINK           = "a";
    public static final String FULLH4RD_NEXT_PAGE      = "a[rel=next]";

    // ================= GEZATEK =================
    public static final String GEZATEK_TITLE = "h4.card-title a";
    public static final String GEZATEK_PRICE = "h4[data-precio]";
    public static final String GEZATEK_IMAGE = "img.img-fluid";
    public static final String GEZATEK_LINK = "div.view.overlay.imagen a, h4.card-title a";
    public static final String GEZATEK_PRODUCT_LIST = "div[class*=item], div.card, div.producto, article";


    // ================= MASTERTECH =================
    public static final String MASTERTECH_PRODUCT_LIST = ".product";
    public static final String MASTERTECH_TITLE        = ".ast-loop-product__link";
    public static final String MASTERTECH_PRICE        = ".price span bdi";
    public static final String MASTERTECH_IMAGE        = "noscript > img";
    public static final String MASTERTECH_LINK         = ".ast-loop-product__link";
    public static final String MASTERTECH_NEXT_PAGE    = "a.next";
}


import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashSet;
import java.io.IOException;
import java.util.HashSet;

public class Webcrawler {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        WebDriver driver = new ChromeDriver();
        try {
            // Apri la pagina
            driver.get("https://www.prozis.com/it/it/nutrizione-sportiva/proteine");

            // Attendi il caricamento degli elementi dinamici (opzionale, con WebDriverWait)
            Thread.sleep(5000); // Pausa per consentire il caricamento degli elementi

            // Trova gli elementi della pagina
            List<WebElement> prodotti = driver.findElements(By.className("product-card"));

            for (WebElement prodotto : prodotti) {
                // Stampa il titolo del prodotto
                String titolo = prodotto.findElement(By.className("product-title")).getText();
                System.out.println("Prodotto: " + titolo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chiudi il browser
            driver.quit();
        }
    }
}

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Webcrawler {

    public static void main(String[] args) {
        // Imposta il percorso del driver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\marco\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        try {
            // Esegui il web scraping per la categoria proteine
            String proteinUrl = "https://www.prozis.com/it/it/nutrizione-sportiva/proteine";
            scrapeProducts(driver, proteinUrl, "Proteine");

            // Esegui il web scraping per la categoria creatina
            String creatineUrl = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/creatina";
            scrapeProducts(driver, creatineUrl, "Creatina");

            String amminoacidiUrl = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/amminoacidi";
            scrapeProducts(driver, amminoacidiUrl, "Amminoacidi");

            String preworkout = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/pre-allenamento-e-ossido-nitrico";
            scrapeProducts(driver, preworkout, "Preworkout");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chiudi il driver
            driver.quit();
        }
    }

    /**
     * Funzione per eseguire il web scraping dei prodotti da una data URL.
     *
     * @param driver WebDriver da utilizzare
     * @param url    URL della categoria di prodotti
     * @param category Nome della categoria per riferimento
     */
    public static void scrapeProducts(WebDriver driver, String url, String category) {
        try {
            // Determina l'XPath corretto in base alla categoria
            String xpath;
            if (category.equalsIgnoreCase("Proteine")) {
                xpath = "//*[@id=\"listSectionWrapper\"]/div[1]/div";
            } else if (category.equalsIgnoreCase("Creatina")) {
                xpath = "//*[@id=\"listSectionWrapper\"]/div/div";
            }else if(category.equalsIgnoreCase("Amminoacidi")){
                xpath = "//*[@id=\"listSectionWrapper\"]/div[1]/div";
            }else if(category.equalsIgnoreCase("Preworkout")){
                xpath = "//*[@id=\"listSectionWrapper\"]/div/div";
            }else {
                System.out.println("Categoria non supportata: " + category);
                return;
            }
            // Apri la pagina della categoria
            driver.get(url);

            // Aspetta che gli elementi siano visibili (usando WebDriverWait)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"listSectionWrapper\"]")));

            // Trova tutti i prodotti nella sezione specificata
            List<WebElement> products = driver.findElements(By.xpath(xpath));

            System.out.println("Categoria: " + category);
            System.out.println("-------------------------------");

            // Itera su ciascun prodotto e raccogli i dati
            for (WebElement product : products) {
                try {
                    // Estrai i dati per ogni prodotto
                    String productName = product.findElement(By.cssSelector(".description")).getText(); // Nome del prodotto
                    String price = product.findElement(By.cssSelector(".price")).getText(); // Prezzo
                    String productUrl = product.findElement(By.cssSelector("a")).getAttribute("href"); // URL del prodotto

                    // Stampa i risultati
                    System.out.println("Prodotto: " + productName);
                    System.out.println("Prezzo: " + price);
                    System.out.println("URL: " + productUrl);
                    System.out.println("-------------------------------");
                } catch (Exception e) {
                    // Gestisci eventuali eccezioni per prodotti senza dati completi
                    System.out.println("Errore nell'elaborazione del prodotto: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Errore durante lo scraping della categoria " + category + ": " + e.getMessage());
        }
    }
}

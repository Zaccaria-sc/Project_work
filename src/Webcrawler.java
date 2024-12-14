import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            scrapeProductsProzis(driver, proteinUrl, "Proteine");

            // Esegui il web scraping per la categoria creatina
            String creatineUrl = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/creatina";
            scrapeProductsProzis(driver, creatineUrl, "Creatina");

            String amminoacidiUrl = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/amminoacidi";
            scrapeProductsProzis(driver, amminoacidiUrl, "Amminoacidi");

            String preworkout = "https://www.prozis.com/it/it/nutrizione-sportiva/aumento-della-massa-muscolare/pre-allenamento-e-ossido-nitrico";
            scrapeProductsProzis(driver, preworkout, "Preworkout");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chiudi il driver
            driver.quit();

        }
    }
    public static void scrapeProductsProzis(WebDriver driver, String url, String category) {
        Database database = new Database("localhost", "3306", "project_work", "root", "");
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
                    String productName = product.findElement(By.cssSelector(".description")).getText(); // Nome del prodotto
                    // Supponiamo che il prezzo sia una stringa nel formato "€123,45" o "123.45"
                    String price = product.findElement(By.cssSelector(".price")).getText(); // Prezzo
                    // Rimuovi eventuali simboli di valuta o spazi
                    price = price.replaceAll("[^\\d,\\.]", "");
                    // Sostituisci la virgola con un punto (se necessario, per il formato decimale)
                    price = price.replace(",", ".");
                    String productUrl = product.findElement(By.cssSelector("a")).getAttribute("href"); // URL del prodotto

                    LocalDateTime currentDateTime = LocalDateTime.now();
                    // Converte il LocalDateTime in Timestamp
                    Timestamp timestamp = Timestamp.valueOf(currentDateTime);

                    // Controlla se il prodotto esiste già nel database
                    if (database.isProductExist(productName, category) && database.isSitoExist(productName, productUrl)) {
                        System.out.println("Il prodotto " + productName + " è già presente nel database.");
                        continue; // Salta l'inserimento se il prodotto è già presente
                    }

                    // Stampa i risultati
                    System.out.println("Prodotto: " + productName);
                    System.out.println("Prezzo: " + price);
                    System.out.println("URL: " + productUrl);
                    System.out.println("-------------------------------");

                    // Inserisce il prodotto nella tabella "prodotti"
                    database.InsertProdotto(productName, category, "Prozis", timestamp);

                    // Inserisce il sito nella tabella "siti"
                    database.InsertSiti(productName, productUrl, timestamp);

                    // Recupera l'ID del prodotto appena inserito
                    int productId = database.getProductIdByName(productName); // Metodo che recupera l'ID del prodotto

                    // Recupera l'ID del sito appena inserito
                    int siteId = database.getSiteIdByUrl(productUrl); // Metodo che recupera l'ID del sito
                    BigDecimal prezzo = new BigDecimal(price);
                    // Inserisci l'offerta nella tabella "offerte"
                    database.InsertOfferta(siteId, productId, prezzo, productUrl, timestamp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Errore durante lo scraping della categoria " + category + ": " + e.getMessage());
        }
    }
    public static void scrapeProductsBulk(WebDriver driver, String url, String category) {
        try {
            // Apri il sito web
            driver.get(url);

            // Aspetta che la pagina sia completamente caricata
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li.product-card")));

            // Trova tutti i prodotti nella pagina
            List<WebElement> products = driver.findElements(By.cssSelector("li.product-card"));

            System.out.println("Categoria: " + category);
            System.out.println("------------------------------------");

            // Estrai i dati per ogni prodotto
            for (WebElement product : products) {
                try {
                    // Nome del prodotto
                    WebElement titleElement = product.findElement(By.cssSelector("h3.product-name"));
                    String productName = titleElement.getText();

                    // Prezzo ridotto del prodotto
                    WebElement priceElement = product.findElement(By.cssSelector("span.reduced-price"));
                    String productPrice = priceElement.getText();

                    // Link del prodotto
                    WebElement linkElement = product.findElement(By.tagName("a"));
                    String productLink = linkElement.getAttribute("href");

                    // Completa il link se è relativo
                    if (!productLink.startsWith("http")) {
                        productLink = "https://www.bulk.com" + productLink;
                    }

                    // Stampa le informazioni
                    System.out.println("Nome prodotto: " + productName);
                    System.out.println("Prezzo prodotto: " + productPrice);
                    System.out.println("Link prodotto: " + productLink);
                    System.out.println("------------------------------------");

                } catch (Exception e) {
                    // Gestisci eventuali eccezioni per prodotti senza dati completi
                    System.out.println("Errore nell'elaborazione di un prodotto: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

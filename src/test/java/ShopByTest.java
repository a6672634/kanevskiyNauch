import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import engine.Item;
import engine.MainShopForm;
import engine.NotebookForm;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
// import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriverService;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import static com.codeborne.selenide.Selenide.open;
import static engine.Utils.browserMaximize;
import static com.codeborne.selenide.Selenide.screenshot;


public class ShopByTest {

    private static final String CHROME_BINARY = "DRIVER/GoogleChromePortable.exe";
    private static final String PATH_TO_CHROMEDRIVER_EXE ="DRIVER/chromedriver.exe";


    /**
     * execute for each test, before executing test
     *
     */

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", PATH_TO_CHROMEDRIVER_EXE);
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
        ChromeOptions options = new ChromeOptions();
        options.setBinary(CHROME_BINARY);

        WebDriverRunner.setWebDriver(new ChromeDriver(ChromeDriverService.createDefaultService(),options));
    }


    private Logger logger = LoggerFactory.getLogger(ShopByTest.class);

    /**
     * Open shop.by and verify
     */
     @Test
    public void shopBySearchTest() throws InterruptedException {

        //Step 1
        //Открыть главную страницу сайта и развернуть браузер
        open("https://shop.by/");
        browserMaximize();
        logger.info("Browser is opened and maximized");
        screenshot("Browser_opened_maximized");

        //Step 2
        //Перейти в раздел с ноутбуками
        MainShopForm mainShopForm = new MainShopForm();

        mainShopForm.selectMenuItem("Компьютеры");
        mainShopForm.selectMenuItem("Ноутбуки");

        NotebookForm notebookForm = new NotebookForm();
        logger.info("Notebook page is opened");
        screenshot("Notebook_page_opened");

        //Step 3
        //Задать фильтры
        notebookForm.clickToExtendManufacturers();
        notebookForm.setFilterForNotebookPage("Dell");
        notebookForm.setFilterForNotebookPage("Lenovo");
        notebookForm.setFilterForNotebookPage("HP");

        notebookForm.setMinPriceNotebookPage("700");
        notebookForm.setMaxPriceNotebookPage("1500");

        notebookForm.clickToExtendScreenSizes();
        notebookForm.setFilterForNotebookPage("12");
        notebookForm.setFilterForNotebookPage("12.1");
        notebookForm.setFilterForNotebookPage("12.5");
        notebookForm.setFilterForNotebookPage("13");
        notebookForm.setFilterForNotebookPage("13.1");
        notebookForm.setFilterForNotebookPage("13.3");
        notebookForm.setFilterForNotebookPage("13.4");
        logger.info("Filters are set");

        notebookForm.clickShowResultsButton();
        screenshot("Filters_are_set");

        //Step 4
        //Отсортировать список по возрастанию цены
        notebookForm.openSortMenu();
        notebookForm.sortLowPriceHighPrice();
        screenshot("sortLowPriceHighPrice");

        //Step 5
        //Посчитать результаты и записать в список объектов с результатами

        List<SelenideElement> searchResults = notebookForm.getResultsList();
        logger.info("Results received");
        List<Item> itemList = notebookForm.serializeItems(searchResults);
        logger.info("Number of elements in the list = " + itemList.size());

        Item firstItem = itemList.get(0);
        logger.info(firstItem.getName());
        logger.info(firstItem.getPrice());
        screenshot("Number_elements_list");

        //Step 6
        // Отсортировать наоборот
        notebookForm.openSortMenu();
        notebookForm.sortHighPriceLowPrice();
        screenshot("sortHighPriceLowPrice");

        //Step 7
        //Перейти на последнюю страницу и найти последний элемент
        notebookForm.getLastPagePagination().click();
        List<SelenideElement> newSearchResults = notebookForm.getResultsList();
        logger.info("Results from last page received");
        List<Item> newItemList = notebookForm.serializeItems(newSearchResults);
        Item lastItem = newItemList.get(newItemList.size()-1);
        logger.info(lastItem.getName());
        logger.info(lastItem.getPrice());
        screenshot("lastElement");

        //Step 8
        //Сравнить первый и последний элементы
        Assert.assertEquals("Check if item's name is the same", firstItem.getName(), lastItem.getName());
        Assert.assertEquals("Check if item's price is the same", firstItem.getPrice(), lastItem.getPrice());
        screenshot("Check_item_NameAndPrice_same");
    }

    @AfterMethod
    public void tearDown() {
        WebDriverRunner.getWebDriver().quit();
    }

}

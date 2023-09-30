import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
        plugin = {
                "pretty",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        features = "src/test/resources/features",
        glue = ""
)
public class CumBer extends AbstractTestNGCucumberTests {

        @BeforeClass
        public static void setup() {
                RestClass.tokenFromStep = RestClass.getToken(RestClass.userName, RestClass.password);
                RestClass.userIdFromStep = RestClass.getUserId(RestClass.userName, RestClass.password);
                RestClass.isbnBookNamesFromCatalogFromStep = RestClass.getIsbnNamesFromCatalog();
                System.out.println("------[ SETUP IS COMPLETED ]------");
        }

}

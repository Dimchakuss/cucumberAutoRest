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
public class CucumberRunner extends AbstractTestNGCucumberTests {

        @BeforeClass
        public static void setup() {
                //RestClass.isbnBookNamesFromCatalogFromStep = RestClass.getIsbnNamesFromCatalog();
                //System.out.println("------[ SETUP IS COMPLETED ]------");
        }

}

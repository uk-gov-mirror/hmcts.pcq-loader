package uk.gov.hmcts.reform.pcqloader.postdeploy;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.pcqloader.PcqLoaderComponent;
import uk.gov.hmcts.reform.pcqloader.config.TestApplicationConfiguration;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@ActiveProfiles("functional")
@Slf4j
public class PcqLoaderFunctionalTest extends PcqLoaderTestBase {

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    @Value("${pcqBackendUrl}")
    private final String pcqBackendUrl;

    @SuppressWarnings({"PMD.UnusedPrivateField"})
    @Value("${jwt_test_secret}")
    private String jwtSecretKey;

    @Autowired
    private PcqLoaderComponent pcqLoaderComponent;

    public PcqLoaderFunctionalTest(String pcqBackendUrl) {
        super();
        this.pcqBackendUrl = pcqBackendUrl;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteMethod() throws IOException, IllegalAccessException {
        //Invoke the executor
        pcqLoaderComponent.execute();
    }
}

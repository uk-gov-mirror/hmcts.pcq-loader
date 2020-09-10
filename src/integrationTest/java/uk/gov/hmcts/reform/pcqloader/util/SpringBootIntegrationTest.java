package uk.gov.hmcts.reform.pcqloader.util;

import com.azure.storage.blob.BlobServiceClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.pcqloader.PcqLoaderComponent;
import uk.gov.hmcts.reform.pcqloader.config.BlobStorageConfiguration;
import uk.gov.hmcts.reform.pcqloader.config.TestApplicationConfiguration;
import uk.gov.hmcts.reform.pcqloader.controller.feign.PcqBackendFeignClient;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.services.impl.PcqBackendServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@SuppressWarnings({"PMD.AbstractClassWithoutAnyMethod", "PMD.AbstractClassWithoutAbstractMethod",
    "PMD.UnusedPrivateField"})
public abstract class SpringBootIntegrationTest {

    @Autowired
    protected PcqBackendServiceImpl pcqBackendServiceImpl;

    @Autowired
    protected PcqBackendFeignClient pcqBackendFeignClient;

    @MockBean
    protected PcqLoaderComponent pcqLoaderComponent;

    @MockBean
    protected BlobStorageManager blobStorageManager;

    @MockBean
    protected BlobStorageConfiguration blobStorageConfiguration;

    @MockBean
    protected BlobServiceClient blobServiceClient;

}

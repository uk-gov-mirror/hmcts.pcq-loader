package uk.gov.hmcts.reform.pcqloader.postdeploy;

import com.azure.storage.blob.BlobContainerClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.pcqloader.PcqLoaderComponent;
import uk.gov.hmcts.reform.pcqloader.config.TestApplicationConfiguration;
import uk.gov.hmcts.reform.pcqloader.helper.PayloadMappingHelper;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.services.BlobStorageManager;
import uk.gov.hmcts.reform.pcqloader.services.PcqBackendService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@ActiveProfiles("functional")
@Slf4j
public class PcqLoaderFunctionalTest extends PcqLoaderTestBase {

    private static final String FUNC_TEST_PCQ_CONTAINER_NAME = "pcq-func-tests";
    private static final String BLOB_FILENAME_1 = "1579002492_31-08-2020-11-35-10.zip";
    private static final String BLOB_FILENAME_2 = "1579002493_31-08-2020-11-48-42.zip";
    private static final String FAIL_ASSERT_MSG = "Method call failed.";

    @Value("${pcqBackendUrl}")
    private String pcqBackendUrl;

    @Autowired
    private PcqLoaderComponent pcqLoaderComponent;

    @Autowired
    private BlobStorageManager blobStorageManager;

    @Autowired
    private PayloadMappingHelper payloadMappingHelper;

    @Autowired
    private PcqBackendService pcqBackendService;

    @Before
    public void beforeTests() throws FileNotFoundException {
        log.info("Starting PcqLoaderComponent functional tests");

        // Create test container
        BlobContainerClient blobContainerClient = blobStorageManager.createContainer(FUNC_TEST_PCQ_CONTAINER_NAME);
        log.info("Created test container: {}", blobContainerClient.getBlobContainerUrl());

        // Upload sample documents
        File blobFile1 = ResourceUtils.getFile("classpath:BlobTestFiles/" + BLOB_FILENAME_1);
        File blobFile2 = ResourceUtils.getFile("classpath:BlobTestFiles/" + BLOB_FILENAME_2);

        blobStorageManager.uploadFileToBlobStorage(blobContainerClient, blobFile1.getPath());
        blobStorageManager.uploadFileToBlobStorage(blobContainerClient, blobFile2.getPath());
    }

    @After
    public void afterTests() {
        log.info("Stopping PcqLoaderComponent functional tests");
        blobStorageManager.deleteContainer(FUNC_TEST_PCQ_CONTAINER_NAME);
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void testAllSuccessfulPcqInvocation() throws IOException {
        String metaDataPayLoad = jsonStringFromFile("JsonTestFiles/successMetaFile.json");
        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        Assertions.assertNotNull(mappedAnswers.getDcnNumber(), "DCN Number is invalid");
        if (mappedAnswers != null) {
            mappedAnswers.setPcqId(UUID.randomUUID().toString());
            mappedAnswers.setDcnNumber("DCN " + UUID.randomUUID().toString());

            ResponseEntity responseEntity = pcqBackendService.submitAnswers(mappedAnswers);
            Assertions.assertEquals(201, responseEntity.getStatusCode().value(),
                                    "Invalid Response");

            PcqAnswerRequest answerRecord = getTestAnswerRecord(mappedAnswers.getPcqId(), pcqBackendUrl);
            Assertions.assertNotNull(answerRecord, "Answer Record is null");
            checkAssertionsOnResponse(answerRecord, mappedAnswers);
        }
    }

    @Test
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public void testAllMultiplePcqInvocation() throws IOException {
        String metaDataPayLoad = jsonStringFromFile("JsonTestFiles/multipleReligionMetaFile.json");
        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        Assertions.assertNotNull(mappedAnswers.getDcnNumber(), "DCN Number is invalid");
        if (mappedAnswers != null) {
            mappedAnswers.setPcqId(UUID.randomUUID().toString());
            mappedAnswers.setDcnNumber("DCN " + UUID.randomUUID().toString());

            ResponseEntity responseEntity = pcqBackendService.submitAnswers(mappedAnswers);
            Assertions.assertEquals(201, responseEntity.getStatusCode().value(),
                                    "Invalid Response");

            PcqAnswerRequest answerRecord = getTestAnswerRecord(mappedAnswers.getPcqId(), pcqBackendUrl);
            Assertions.assertNotNull(answerRecord, "Answer Record is null");
            checkAssertionsOnResponse(answerRecord, mappedAnswers);
        }
    }


    @Test
    public void testExecuteMethod() {
        //Invoke the executor
        pcqLoaderComponent.execute();
    }


}

package uk.gov.hmcts.reform.pcqloader.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Base64Utils;
import uk.gov.hmcts.reform.pcqloader.config.TestSupportUtils;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.model.PcqMetaData;
import uk.gov.hmcts.reform.pcqloader.model.PcqPayLoad;
import uk.gov.hmcts.reform.pcqloader.utils.AssertionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.DataflowAnomalyAnalysis"})
public class PayloadMappingHelperTest {

    private static final String FAIL_ASSERT_MSG = "Method call failed.";

    @Spy
    @SuppressWarnings("PMD.UnusedPrivateField")
    private final PayloadValidationHelper payloadValidationHelper = new PayloadValidationHelper();

    @InjectMocks
    private PayloadMappingHelper payloadMappingHelper;

    private final AssertionUtils assertUtils = new AssertionUtils();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mapPayLoadSuccess() throws IOException {
        String dcnNumber = "11003402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/successMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.SUCCESS_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertSuccessMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void mapPayLoadMultipleIntElements() throws IOException {
        String dcnNumber = "12003402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/multipleReligionMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.MULTIPLE_RELIGION_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertReligionInvalidMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void mapPayLoadInvalidDob() throws IOException {
        String dcnNumber = "13003402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidDobMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.INVALID_DOB_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDobInvalidMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void mapPayLoadDisabilityNone() throws IOException {
        String dcnNumber = "14103402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/disabilityNoneMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.DISABILITY_NONE_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDisabilityNoneMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void mapMultipleEthnicity() throws IOException {
        String dcnNumber = "26103402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/multipleEthnicityMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.MULTIPLE_ETHNICITY_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertMultipleEthnicityMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void mapInvalidOtherValues() throws IOException {
        String dcnNumber = "287103402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherValuesMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.INVALID_OTHER_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertUtils.assertInvalidOtherMapping(mappedAnswers, metaData, dcnNumber);
    }

    @Test
    public void payloadScannableItemMissing() throws IOException {
        String dcnNumber = "11003403";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/noScannableItemsMetaFile.json");

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertNull(mappedAnswers, "Mapped Answers should be null");

    }

    @Test
    public void jsonProcessingError()  {
        String dcnNumber = "11003403";

        String metaDataPayLoad = "{Test:asdsad}";

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertNull(mappedAnswers, "Mapped Answers should be null");

    }


    /**
     * Obtains a JSON String from a JSON file in the classpath (Resources directory).
     *
     * @param fileName - The name of the Json file from classpath.
     * @return - JSON String from the file.
     * @throws IOException - If there is any issue when reading from the file.
     */
    public static String jsonStringFromFile(String fileName) throws IOException {
        File resource = new ClassPathResource(fileName).getFile();
        return new String(Files.readAllBytes(resource.toPath()));
    }

    public static PcqPayLoad jsonPayloadObjectFromString(String jsonString) throws IOException {
        return new ObjectMapper().readValue(jsonString, PcqPayLoad.class);
    }

    public static PcqMetaData jsonMetaDataObjectFromString(String jsonString) throws IOException {
        return new ObjectMapper().readValue(jsonString, PcqMetaData.class);
    }



}

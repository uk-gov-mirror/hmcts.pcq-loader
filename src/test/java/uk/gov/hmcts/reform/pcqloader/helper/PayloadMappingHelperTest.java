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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.DataflowAnomalyAnalysis"})
public class PayloadMappingHelperTest {

    private static final String FAIL_ASSERT_MSG = "Method call failed.";
    private static final String ENGLISH_LANGUAGE_LEVEL_MSG = "English Language Level is not correct";

    @Spy
    @SuppressWarnings("PMD.UnusedPrivateField")
    private final PayloadValidationHelper payloadValidationHelper = new PayloadValidationHelper();

    @InjectMocks
    private PayloadMappingHelper payloadMappingHelper;

    private final AssertionUtils assertUtils = new AssertionUtils();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void mapPayLoadSuccess() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/successMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.SUCCESS_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertSuccessMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadSuccessEmptyDobProvided() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/successEmptyDobProvidedMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(
            TestSupportUtils.SUCCESS_EMPTY_DOB_PROVIDED_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertSuccessMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadMultipleIntElements() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/multipleReligionMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.MULTIPLE_RELIGION_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertReligionInvalidMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadInvalidDob() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidDobMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.INVALID_DOB_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDobInvalidMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadInvalidDobAndDobProvidedEmpty() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidDobEmptyProvidedMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(
            TestSupportUtils.INVALID_DOB_EMPTY_PROVIDED_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDobInvalidMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadDisabilityNone() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/disabilityNoneMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.DISABILITY_NONE_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDisabilityNoneMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapMultipleEthnicity() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/multipleEthnicityMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.MULTIPLE_ETHNICITY_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertMultipleEthnicityMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapInvalidOtherValues() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherValuesMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.INVALID_OTHER_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertUtils.assertInvalidOtherMapping(mappedAnswers, metaData);
    }

    @Test
    public void payloadScannableItemMissing() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/noScannableItemsMetaFile.json");

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertNull(mappedAnswers, "Mapped Answers should be null");

    }

    @Test
    public void jsonProcessingError()  {

        String metaDataPayLoad = "{Test:asdsad}";

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertNull(mappedAnswers, "Mapped Answers should be null");

    }

    @Test
    public void mapPayLoadDobNotProvided() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/dobNotProvidedMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.DOB_NOT_PROVIDED_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDobInvalidMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadDobEmptyNotProvided() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/dobEmptyNotProvidedMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.DOB_EMPTY_NOT_PROVIDED_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertDobInvalidMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadNoDisabilityCondition() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/noDisabilityMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.NO_DISABILITY_CONDITION_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertNoDisabilityCondition(mappedAnswers, metaData, 2);
    }

    @Test
    public void mapPayLoadNoPreferenceDisabilityCondition() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/notPreferDisabilityMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(
            TestSupportUtils.NOT_PREFER_DISABILITY_CONDITION_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertNoDisabilityCondition(mappedAnswers, metaData, 0);
    }

    @Test
    public void mapPayLoadNoDisabilityImpact() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/noDisabilityImpactMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.NO_DISABILITY_IMPACT_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertNoDisabilityImpact(mappedAnswers, metaData, 3);
    }

    @Test
    public void mapPayLoadNotPreferDisabilityImpact() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/notPreferDisabilityImpactMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(
            TestSupportUtils.NOT_PREFER_DISABILITY_IMPACT_PAYLOAD);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertNoDisabilityImpact(mappedAnswers, metaData, 0);
    }

    @Test
    public void mapPayLoadLanguageLevelEmpty() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/emptyLanguageMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.EMPTY_LANGUAGE_LEVEL);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }


        assertUtils.assertSuccessMapping(mappedAnswers, metaData);
    }

    @Test
    public void mapPayLoadMainLanguageEmpty() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/emptyMainLanguageMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.EMPTY_MAIN_LANGUAGE);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertEquals(1, mappedAnswers.getPcqAnswers().getEnglishLanguageLevel(), ENGLISH_LANGUAGE_LEVEL_MSG);
        assertUtils.assertLanguageMapping(mappedAnswers, metaData, null);
    }

    @Test
    public void mapPayLoadMainLanguageNotPreferred() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/notPreferMainLanguageMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.MAIN_LANG_NOT_PREFERRED);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertNull(mappedAnswers.getPcqAnswers().getEnglishLanguageLevel(), ENGLISH_LANGUAGE_LEVEL_MSG);
        assertUtils.assertLanguageMapping(mappedAnswers, metaData, 0);
    }

    @Test
    public void mapPayLoadMainLanguageOther() throws IOException {

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/otherMainLanguageMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqPayLoad expectedPcqPayload = jsonPayloadObjectFromString(TestSupportUtils.OTHER_MAIN_LANGUAGE);
        PcqPayLoad actualPayLoad = jsonPayloadObjectFromString(new String(Base64Utils.decodeFromString(
            metaData.getScannableItems()[0].getOcrData())));
        // Assert the expected and actual payloads are correct before invoking the mapping.
        assertUtils.assertPayLoads(expectedPcqPayload, actualPayLoad);

        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }

        assertUtils.assertOtherLanguageMapping(mappedAnswers, metaData);
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

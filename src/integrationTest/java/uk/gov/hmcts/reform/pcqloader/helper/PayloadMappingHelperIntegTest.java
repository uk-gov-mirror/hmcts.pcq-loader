package uk.gov.hmcts.reform.pcqloader.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;
import uk.gov.hmcts.reform.pcqloader.model.PcqMetaData;
import uk.gov.hmcts.reform.pcqloader.model.PcqScannableItems;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class PayloadMappingHelperIntegTest {

    private static final String FAIL_ASSERT_MSG = "Method call failed.";
    private static final String FORM_ID_VALIDATION_MSG = "Form Id is not correct.";
    private static final String SERVICE_ID_VALIDATION_MSG = "Service Id is not correct.";
    private static final String EXPECTED_DOB = "2000-01-01T00:00:00.000Z";
    private static final String DOB_VALIDATION_MSG = "Dob is not correct";

    private PayloadMappingHelper payloadMappingHelper;

    private PayloadValidationHelper payloadValidationHelper = new PayloadValidationHelper();

    @BeforeEach
    public void setUp() {
        payloadMappingHelper = new PayloadMappingHelper(payloadValidationHelper);
    }

    @Test
    @DisplayName("Payload cannot be decrypted.")
    public void testBadDecryption() throws IOException {
        String dcnNumber = "11003402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/decryptErrorMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);

        assertNull(mappedAnswers, "No mapping should be done.");
    }

    @Test
    @DisplayName("Multiple choices test.")
    public void testMultipleChoices() throws IOException {
        String dcnNumber = "1100323402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/multipleChoiceMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);

        assertInvalid(mappedAnswers.getPcqAnswers().getLanguageMain(), "Language_Main");
        assertInvalid(mappedAnswers.getPcqAnswers().getEnglishLanguageLevel(), "English Language Level");
        assertInvalid(mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity");
        assertInvalid(mappedAnswers.getPcqAnswers().getDisabilityConditions(), "Disability Conditions");
        assertInvalid(mappedAnswers.getPcqAnswers().getDisabilityImpact(), "Disability Impact");
        assertInvalid(mappedAnswers.getPcqAnswers().getPregnancy(), "Pregnancy");
        assertInvalid(mappedAnswers.getPcqAnswers().getSexuality(), "Sexuality");
        assertInvalid(mappedAnswers.getPcqAnswers().getSex(), "Sex");
        assertInvalid(mappedAnswers.getPcqAnswers().getGenderDifferent(), "Gender Different");
        assertInvalid(mappedAnswers.getPcqAnswers().getMarriage(), "Marriage");

    }

    @Test
    @DisplayName("Empty payload - User did not mark anything in the form.")
    public void testEmptyPayload() throws IOException {
        String dcnNumber = "1100323402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/emptyPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);

        assertNullMapping(mappedAnswers.getPcqAnswers());

    }

    @Test
    @DisplayName("Missing Elements from the Payload - Exela/BulkScan error case")
    public void testMissingElementsFromPayload() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/nullPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);

        assertNullMapping(mappedAnswers.getPcqAnswers());
    }

    @Test
    @DisplayName("Other Text Field entered but user has not ticked the Other checkbox.")
    public void testInvalidOtherTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertInvalid(mappedAnswers.getPcqAnswers().getLanguageMain(), "Language_Main");

    }

    @Test
    @DisplayName("OtherWhite and OtherMixed Text Field entered but user has not ticked Only OtherWhite checkbox.")
    public void testInvalidOtherWhiteTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherWhitePayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertInvalid(mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity");
        assertNull(mappedAnswers.getPcqAnswers().getEthnicityOther(), "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("OtherWhite and OtherMixed Text Field entered but user has not ticked Only OtherMixed checkbox.")
    public void testInvalidOtherMixedTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherMixedPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertInvalid(mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity");
        assertNull(mappedAnswers.getPcqAnswers().getEthnicityOther(), "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("OtherWhite and OtherAsian Text Field entered but user has not ticked Only OtherAsian checkbox.")
    public void testInvalidOtherAsianTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherAsianPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertInvalid(mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity");
        assertNull(mappedAnswers.getPcqAnswers().getEthnicityOther(), "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("OtherWhite and OtherCarib Text Field entered but user has not ticked Only OtherCarib checkbox.")
    public void testInvalidOtherCaribTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/invalidOtherCaribPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertInvalid(mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity");
        assertNull(mappedAnswers.getPcqAnswers().getEthnicityOther(), "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("Valid other white ethnicity test")
    public void testValidOtherWhiteTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validOtherWhitePayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertEquals(4, mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity not correct");
        assertEquals("OtherWhite", mappedAnswers.getPcqAnswers().getEthnicityOther(),
                     "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("Valid other mixed ethnicity test")
    public void testValidOtherMixedTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validOtherMixedPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertEquals(8, mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity not correct");
        assertEquals("Other Mixed", mappedAnswers.getPcqAnswers().getEthnicityOther(),
                     "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("Valid other Asian ethnicity test")
    public void testValidOtherAsianTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validOtherAsianPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertEquals(13, mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity not correct");
        assertEquals("Other Asian", mappedAnswers.getPcqAnswers().getEthnicityOther(),
                     "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("Valid other Carib ethnicity test")
    public void testValidOtherCaribTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validOtherCaribPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertEquals(16, mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity not correct");
        assertEquals("Other Carib", mappedAnswers.getPcqAnswers().getEthnicityOther(),
                     "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("Valid other ethnicity test")
    public void testValidOtherEthnicityTextInput() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validOtherEthnicityPayloadMetaFile.json");

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);
        assertEquals(18, mappedAnswers.getPcqAnswers().getEthnicity(), "Ethnicity not correct");
        assertEquals("Other Ethnicity", mappedAnswers.getPcqAnswers().getEthnicityOther(),
                     "Ethnicity Other is not correct.");

    }

    @Test
    @DisplayName("A valid scenario test where user has supplied a valid form.")
    public void testValidFormSubmission() throws IOException {
        String dcnNumber = "1100324402";

        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/validPayloadMetaFile.json");
        PcqMetaData metaData = jsonMetaDataObjectFromString(metaDataPayLoad);

        PcqAnswerRequest mappedAnswers = invokeMappingHelper(dcnNumber, metaDataPayLoad);

        assertSuccessMapping(mappedAnswers, metaData, dcnNumber);
    }

    private PcqAnswerRequest invokeMappingHelper(String dcnNumber, String metaDataPayLoad) {
        PcqAnswerRequest mappedAnswers = null;
        try {
            mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(FAIL_ASSERT_MSG, e);
        }
        return mappedAnswers;
    }

    private void assertInvalid(Integer actualValue, String fieldName) {
        assertEquals(-1, actualValue, fieldName + " not correct.");
    }

    private void assertNullMapping(PcqAnswers answers) {
        assertNull(answers.getLanguageMain(), "Language_Main is not correct.");
        assertNull(answers.getMarriage(), "Marriage is not correct.");
        assertNull(answers.getGenderOther(), "Gender_other is not correct.");
        assertNull(answers.getGenderDifferent(), "Gender_different is not correct.");
        assertNull(answers.getSex(), "Sex is not correct.");
        assertNull(answers.getSexualityOther(), "Sexuality_Other is not correct.");
        assertNull(answers.getSexuality(), "Sexuality is not correct.");
        assertNull(answers.getDisabilityConditionOther(), "Disability_Condition_other is not correct.");
        assertNull(answers.getDisabilityOther(), "Disability_Other is not correct.");
        assertNull(answers.getDisabilitySocial(), "Disability_Social is not correct.");
        assertNull(answers.getDisabilityStamina(), "Disability_Stamina is not correct.");
        assertNull(answers.getDisabilityMentalHealth(), "Disability_Mental_Health is not correct.");
        assertNull(answers.getDisabilityDexterity(), "Disability_Dexterity is not correct.");
        assertNull(answers.getDisabilityMobility(), "Disability_Mobility is not correct.");
        assertNull(answers.getDisabilityVision(), "Disability_Vision is not correct.");
        assertNull(answers.getDisabilityImpact(), "Disability_Impact is not correct.");
        assertNull(answers.getDisabilityConditions(), "Disability_Conditions is not correct.");
        assertNull(answers.getEthnicityOther(), "Ethnicity_Other is not correct.");
        assertNull(answers.getEthnicity(), "Ethnicity is not correct.");
        assertEquals(0, answers.getDobProvided(), "Dob_Provided is not correct.");
        assertNull(answers.getDob(), "Dob is not correct.");
        assertNull(answers.getReligionOther(), "Religion_Other is not correct.");
        assertNull(answers.getReligion(), "Religion is not correct.");
        assertNull(answers.getLanguageOther(), "Language_Other is not correct.");
        assertNull(answers.getEnglishLanguageLevel(), "English_Language_level is not correct.");
        assertNull(answers.getDisabilityHearing(), "Disability_Hearing is not correct.");
        assertNull(answers.getDisabilityLearning(), "Disability_Learning is not correct.");
        assertNull(answers.getDisabilityMemory(), "Disability_Memory is not correct.");
        assertNull(answers.getDisabilityNone(), "Disability_None is not correct.");
        assertNull(answers.getPregnancy(), "Pregnancy is not correct.");
    }

    public void assertSuccessMapping(PcqAnswerRequest mappedAnswers, PcqMetaData pcqMetaData, String dcnNumber) {
        assertDefaultAndGeneratedFields(mappedAnswers, dcnNumber);

        //Check the information that matches the meta-data information.
        PcqScannableItems[] pcqScannedItems = pcqMetaData.getScannableItems();
        assertEquals(pcqScannedItems[0].getDocumentType(), mappedAnswers.getFormId(), FORM_ID_VALIDATION_MSG);
        assertEquals(pcqMetaData.getJurisdiction(), mappedAnswers.getServiceId(), SERVICE_ID_VALIDATION_MSG);

        //Check the answers matches the payload supplied.
        PcqAnswers answers = mappedAnswers.getPcqAnswers();
        assertEquals(1, answers.getLanguageMain(), "Language_Main is not correct.");
        assertNull(answers.getLanguageOther(), "Language_Other is not correct.");
        assertEquals(2, answers.getEnglishLanguageLevel(), "English_Language_level is not correct.");
        assertEquals(3, answers.getReligion(), "Religion is not correct.");
        assertNull(answers.getReligionOther(), "Religion_Other is not correct.");
        assertEquals(EXPECTED_DOB, answers.getDob(),  DOB_VALIDATION_MSG);
        assertEquals(1, answers.getDobProvided(), "Dob_Provided is not correct.");
        assertEquals(5, answers.getEthnicity(), "Ethnicity is not correct.");
        assertNull(answers.getEthnicityOther(), "Ethnicity_Other is not correct.");
        assertEquals(0, answers.getDisabilityConditions(), "Disability_Conditions is not correct.");
        assertEquals(3, answers.getDisabilityImpact(), "Disability_Impact is not correct.");
        assertEquals(1, answers.getDisabilityVision(), "Disability_Vision is not correct.");
        assertEquals(0, answers.getDisabilityHearing(), "Disability_Hearing is not correct.");
        assertEquals(1, answers.getDisabilityMobility(), "Disability_Mobility is not correct.");
        assertEquals(0, answers.getDisabilityDexterity(), "Disability_Dexterity is not correct.");
        assertEquals(1, answers.getDisabilityLearning(), "Disability_Learning is not correct.");
        assertEquals(0, answers.getDisabilityMemory(), "Disability_Memory is not correct.");
        assertEquals(1, answers.getDisabilityMentalHealth(), "Disability_Mental is not correct.");
        assertEquals(0, answers.getDisabilityStamina(), "Disability_Stamina is not correct.");
        assertEquals(1, answers.getDisabilitySocial(), "Disability_Social is not correct.");
        assertEquals(1, answers.getDisabilityOther(), "Disability_Other is not correct.");
        assertEquals("Other disability", answers.getDisabilityConditionOther(),
                     "Disability_Condition_Other is not correct.");
        assertEquals(0, answers.getDisabilityNone(), "Disability_None is not correct.");
        assertEquals(2, answers.getPregnancy(), "Pregnancy is not correct.");
        assertEquals(1, answers.getSexuality(), "Sexuality is not correct.");
        assertNull(answers.getSexualityOther(), "Sexuality_Other is not correct.");
        assertEquals(1, answers.getSex(), "Sex is not correct.");
        assertEquals(1, answers.getGenderDifferent(), "Gender_Different is not correct.");
        assertNull(answers.getGenderOther(), "Gender_other is not correct.");
        assertEquals(2, answers.getMarriage(), "Marriage is not correct.");
    }

    public void assertDefaultAndGeneratedFields(PcqAnswerRequest mappedAnswers, String dcnNumber) {
        //Check the primary key generated field is not missing.
        assertNotNull(mappedAnswers.getPcqId(), "PCQ Id is Null");

        //Check the correct DCN Number is populated.
        assertEquals(dcnNumber, mappedAnswers.getDcnNumber(), "DCN Number is not correct.");

        //Check the default values are set correctly for paper channel
        assertNull(mappedAnswers.getCaseId(), "Case Id is not correct.");
        String expectedPartyId = "PaperForm";
        assertEquals(expectedPartyId, mappedAnswers.getPartyId(), "Party Id is not correct.");
        int expectedChannel = 2;
        assertEquals(expectedChannel, mappedAnswers.getChannel(), "Channel is not correct.");
        assertNotNull(mappedAnswers.getCompletedDate(), "Completed Date is empty");
        String expectedActor = "UNKNOWN";
        assertEquals(expectedActor, mappedAnswers.getActor(), "Actor is not correct.");
        int expectedVersion = 1;
        assertEquals(expectedVersion, mappedAnswers.getVersionNo(), "Version Number is not correct.");
        assertNull(mappedAnswers.getOptOut(), "Opt Out is not correct.");
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

    public static PcqMetaData jsonMetaDataObjectFromString(String jsonString) throws IOException {
        return new ObjectMapper().readValue(jsonString, PcqMetaData.class);
    }

}

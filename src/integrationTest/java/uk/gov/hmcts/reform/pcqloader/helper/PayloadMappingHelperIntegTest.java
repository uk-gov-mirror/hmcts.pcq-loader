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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class PayloadMappingHelperIntegTest {

    private static final String FAIL_ASSERT_MSG = "Method call failed.";


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

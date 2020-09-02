package uk.gov.hmcts.reform.pcqloader.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class PayloadValidationHelperTest {

    private static final String OTHER_LANGUAGE = "Other Language";
    private static final String OTHER_GENDER = "Other Gender";
    private static final String OTHER_SEXUALITY = "Other Sexuality";
    private static final String OTHER_RELIGION = "Other Religion";
    private static final String OTHER_ETHNICITY = "Other Ethnicity";

    @InjectMocks
    private PayloadValidationHelper payloadValidationHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLanguageMainOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setLanguageOther(OTHER_LANGUAGE);

        for (int i = 0; i < 2; i++) {
            answers.setLanguageMain(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getLanguageOther(), "Language_Other is not correct");
            assertEquals(-1, answers.getLanguageMain(), "Language_Main is not correct");

            //re-set the other language for the next text.
            answers.setLanguageOther(OTHER_LANGUAGE);
        }

        //Valid test
        answers.setLanguageMain(2);
        answers.setLanguageOther(OTHER_LANGUAGE);
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(2, answers.getLanguageMain(), "Language_Main is not correct");
        assertEquals(OTHER_LANGUAGE, answers.getLanguageOther(), "Language_Other is not correct");
    }

    @Test
    public void testGenderOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setGenderOther(OTHER_GENDER);

        for (int i = 0; i < 2; i++) {
            answers.setGenderDifferent(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getGenderOther(), "Gender_Other is not correct");
            assertEquals(-1, answers.getGenderDifferent(), "Gender_Different is not correct");

            //re-set the other gender for the next text.
            answers.setGenderOther(OTHER_GENDER);
        }

        //Valid test
        answers.setGenderDifferent(2);
        answers.setGenderOther(OTHER_GENDER);
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(2, answers.getGenderDifferent(), "Gender_Different is not correct");
        assertEquals(OTHER_GENDER, answers.getGenderOther(), "Gender_Other is not correct");
    }

    @Test
    public void testSexualityOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setSexualityOther(OTHER_SEXUALITY);

        for (int i = 0; i < 4; i++) {
            answers.setSexuality(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getSexualityOther(), "Sexuality_Other is not correct");
            assertEquals(-1, answers.getSexuality(), "Sexuality is not correct");

            //re-set the other sexuality for the next text.
            answers.setSexualityOther(OTHER_SEXUALITY);
        }

        //Valid test
        answers.setSexuality(4);
        answers.setSexualityOther(OTHER_SEXUALITY);
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(4, answers.getSexuality(), "Sexuality is not correct");
        assertEquals(OTHER_SEXUALITY, answers.getSexualityOther(), "Sexuality_Other is not correct");
    }

    @Test
    public void testReligionOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setReligionOther(OTHER_RELIGION);

        for (int i = 0; i < 8; i++) {
            answers.setReligion(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getReligionOther(), "Religion_Other is not correct");
            assertEquals(-1, answers.getReligion(), "Religion is not correct");

            //re-set the other Religion for the next text.
            answers.setReligionOther(OTHER_RELIGION);
        }

        //Valid test
        answers.setReligion(8);
        answers.setReligionOther(OTHER_RELIGION);
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(8, answers.getReligion(), "Religion is not correct");
        assertEquals(OTHER_RELIGION, answers.getReligionOther(), "Religion_Other is not correct");
    }

    @Test
    public void tesEthnicityOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setEthnicityOther(OTHER_ETHNICITY);

        //Test the Other White scenario.
        checkAndAssertEthnicity(0, 4, answers);
        //Test the Other Mixed scenario
        checkAndAssertEthnicity(5, 8, answers);
        // Test the Other Asian scenario
        checkAndAssertEthnicity(9, 13, answers);
        // Test the Other African Caribbean scenarios
        checkAndAssertEthnicity(14, 16, answers);
        // Test the Other scenario
        checkAndAssertEthnicity(18,18, answers);

    }

    private void checkAndAssertEthnicity(int startIndex, int endIndex, PcqAnswers answers) {
        for (int i = startIndex; i < endIndex; i++) {
            answers.setEthnicity(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getEthnicityOther(), "Ethnicity_Other is not correct");
            assertEquals(-1, answers.getEthnicity(), "Ethnicity is not correct");

            //re-set the other sexuality for the next text.
            answers.setEthnicityOther(OTHER_ETHNICITY);
        }

        //Valid test
        answers.setEthnicity(endIndex);
        answers.setEthnicityOther(OTHER_ETHNICITY);
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(endIndex, answers.getEthnicity(), "Ethnicity is not correct");
        assertEquals(OTHER_ETHNICITY, answers.getEthnicityOther(), "Ethnicity_Other is not correct");
    }

}

package uk.gov.hmcts.reform.pcqloader.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class PayloadValidationHelperTest {

    @InjectMocks
    PayloadValidationHelper payloadValidationHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLanguageMainOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setLanguageOther("Other Language");

        for (int i = 0; i < 2; i++) {
            answers.setLanguageMain(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getLanguageOther(), "Language_Other is not correct");
            assertEquals(-1, answers.getLanguageMain(), "Language_Main is not correct");

            //re-set the other language for the next text.
            answers.setLanguageOther("Other Language");
        }

        //Valid test
        answers.setLanguageMain(2);
        answers.setLanguageOther("Other Language");
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(2, answers.getLanguageMain(), "Language_Main is not correct");
        assertEquals("Other Language", answers.getLanguageOther(), "Language_Other is not correct");
    }

    @Test
    public void testGenderOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setGenderOther("Other Gender");

        for (int i = 0; i < 2; i++) {
            answers.setGenderDifferent(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getGenderOther(), "Gender_Other is not correct");
            assertEquals(-1, answers.getGenderDifferent(), "Gender_Different is not correct");

            //re-set the other gender for the next text.
            answers.setGenderOther("Other Gender");
        }

        //Valid test
        answers.setGenderDifferent(2);
        answers.setGenderOther("Other Gender");
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(2, answers.getGenderDifferent(), "Gender_Different is not correct");
        assertEquals("Other Gender", answers.getGenderOther(), "Gender_Other is not correct");
    }

    @Test
    public void testSexualityOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setSexualityOther("Other Sexuality");

        for (int i = 0; i < 4; i++) {
            answers.setSexuality(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getSexualityOther(), "Sexuality_Other is not correct");
            assertEquals(-1, answers.getSexuality(), "Sexuality is not correct");

            //re-set the other sexuality for the next text.
            answers.setSexualityOther("Other Sexuality");
        }

        //Valid test
        answers.setSexuality(4);
        answers.setSexualityOther("Other Sexuality");
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(4, answers.getSexuality(), "Sexuality is not correct");
        assertEquals("Other Sexuality", answers.getSexualityOther(), "Sexuality_Other is not correct");
    }

    @Test
    public void testReligionOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setReligionOther("Other Religion");

        for (int i = 0; i < 8; i++) {
            answers.setReligion(i);
            payloadValidationHelper.validateAndCorrectOtherFields(answers);
            assertNull(answers.getReligionOther(), "Religion_Other is not correct");
            assertEquals(-1, answers.getReligion(), "Religion is not correct");

            //re-set the other Religion for the next text.
            answers.setReligionOther("Other Religion");
        }

        //Valid test
        answers.setReligion(8);
        answers.setReligionOther("Other Religion");
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(8, answers.getReligion(), "Religion is not correct");
        assertEquals("Other Religion", answers.getReligionOther(), "Religion_Other is not correct");
    }

    @Test
    public void tesEthnicityOtherFields() {
        PcqAnswers answers = new PcqAnswers();
        answers.setEthnicityOther("Other Ethnicity");

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
            answers.setEthnicityOther("Other Ethnicity");
        }

        //Valid test
        answers.setEthnicity(endIndex);
        answers.setEthnicityOther("Other Ethnicity");
        payloadValidationHelper.validateAndCorrectOtherFields(answers);
        assertEquals(endIndex, answers.getEthnicity(), "Ethnicity is not correct");
        assertEquals("Other Ethnicity", answers.getEthnicityOther(), "Ethnicity_Other is not correct");
    }

}

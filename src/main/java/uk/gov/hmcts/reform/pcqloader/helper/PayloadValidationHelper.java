package uk.gov.hmcts.reform.pcqloader.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class PayloadValidationHelper {

    private final String[] submitAnswerOtherFields = {"languageOther", "genderOther", "sexualityOther",
        "ethnicityOther", "religionOther"};

    private final String[] submitAnswerOtherIntFieldValues = {"2", "2", "4", "4,8,13,16,18", "8"};

    public void validateDisabilityNone(PcqAnswers answers) {
        // If the disability_none has value of "1" then all other disabilities should be set to null.
        if (answers.getDisabilityNone() != null && answers.getDisabilityNone() > 0) {
            answers.setDisabilityVision(null);
            answers.setDisabilityHearing(null);
            answers.setDisabilityMobility(null);
            answers.setDisabilityDexterity(null);
            answers.setDisabilityLearning(null);
            answers.setDisabilityMemory(null);
            answers.setDisabilityMentalHealth(null);
            answers.setDisabilityStamina(null);
            answers.setDisabilitySocial(null);
            answers.setDisabilityOther(null);
            answers.setDisabilityConditionOther(null);
            log.info("Setting disability values to null because disability None value is 1");
        }
    }

    public void validateAndCorrectOtherFields(PcqAnswers answers) {
        for (int i = 0; i < submitAnswerOtherFields.length; i++) {
            switch (submitAnswerOtherFields[i]) {
                case "languageOther" :
                    validateOtherLanguage(answers, i);
                    break;
                case "genderOther" :
                    validateOtherGender(answers, i);
                    break;
                case "sexualityOther" :
                    validateOtherSexuality(answers, i);
                    break;
                case "ethnicityOther" :
                    validateOtherEthnicity(answers, i);
                    break;
                case "religionOther" :
                    validateOtherReligion(answers, i);
                    break;
                default :
                    break;
            }
        }
    }

    public void validateOtherLanguage(PcqAnswers answers, int index) {
        if (answers.getLanguageOther() != null
            && answers.getLanguageMain() != null && !submitAnswerOtherIntFieldValues[index]
            .contains(String.valueOf(answers.getLanguageMain()))) {
            answers.setLanguageOther(null);
            answers.setLanguageMain(-1);
            log.error("Invalid other language provided, user has supplied duplicate values. "
                          + "Setting language_main to -1");
        }
    }

    public void validateOtherGender(PcqAnswers answers, int index) {
        if (answers.getGenderOther() != null
            && answers.getGenderDifferent() != null && !submitAnswerOtherIntFieldValues[index]
            .contains(String.valueOf(answers.getGenderDifferent()))) {
            answers.setGenderOther(null);
            answers.setGenderDifferent(-1);
            log.error("Invalid other gender provided, user has supplied duplicate values. "
                          + "Setting gender_different to -1");
        }
    }

    public void validateOtherSexuality(PcqAnswers answers, int index) {
        if (answers.getSexualityOther() != null
            && answers.getSexuality() != null && !submitAnswerOtherIntFieldValues[index]
            .contains(String.valueOf(answers.getSexuality()))) {
            answers.setSexualityOther(null);
            answers.setSexuality(-1);
            log.error("Invalid other sexuality provided, user has supplied duplicate values. "
                          + "Setting sexuality to -1");
        }
    }

    public void validateOtherEthnicity(PcqAnswers answers, int index) {
        if (answers.getEthnicityOther() != null && answers.getEthnicity() != null) {
            List<String> splitList = Arrays.asList(submitAnswerOtherIntFieldValues[index].split(","));
            if (! splitList.contains(String.valueOf(answers.getEthnicity()))) {
                answers.setEthnicityOther(null);
                answers.setEthnicity(-1);
                log.error("Invalid ethnicity provided, user has supplied duplicate values. "
                              + "Setting ethnicity to -1");
            }
        }
    }

    public void validateOtherReligion(PcqAnswers answers, int index) {
        if (answers.getReligionOther() != null
            && answers.getReligion() != null && !submitAnswerOtherIntFieldValues[index]
            .contains(String.valueOf(answers.getReligion()))) {
            answers.setReligionOther(null);
            answers.setReligion(-1);
            log.error("Invalid other religion provided, user has supplied duplicate values. "
                          + "Setting religion to -1");
        }
    }

    public boolean isDobProvided(PcqAnswers answers) {
        if (answers.getDobProvided() != null && answers.getDobProvided() == 0) {
            answers.setDob(null);
            return false;
        }

        return true;
    }

}

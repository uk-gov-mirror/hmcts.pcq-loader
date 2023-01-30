package uk.gov.hmcts.reform.pcqloader.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import uk.gov.hmcts.reform.pcq.commons.model.PcqAnswers;
import uk.gov.hmcts.reform.pcq.commons.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcq.commons.model.PcqMetaData;
import uk.gov.hmcts.reform.pcq.commons.model.PcqPayLoad;
import uk.gov.hmcts.reform.pcq.commons.model.PcqPayloadContents;
import uk.gov.hmcts.reform.pcq.commons.model.PcqScannableItems;
import uk.gov.hmcts.reform.pcq.commons.utils.PcqUtils;

//import java.lang.reflect.Field;
import static uk.gov.hmcts.reform.pcq.commons.utils.PcqUtils.nullIfEmpty;

@Component
@Slf4j
public class PayloadMappingHelper {

    private static final String EMPTY_STRING = "";

    private final PayloadValidationHelper payloadValidationHelper;

    @Autowired
    public PayloadMappingHelper(PayloadValidationHelper payloadValidationHelper) {
        this.payloadValidationHelper = payloadValidationHelper;
    }

    public PcqAnswerRequest mapPayLoadToPcqAnswers(String metaDataString) throws
        NoSuchFieldException, IllegalAccessException {

        try {
            // Step 1. Convert the JSon String into an Java Object
            PcqMetaData pcqMetaData = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(metaDataString, PcqMetaData.class);

            // Step 2. Retrieve the Scannable items
            PcqScannableItems[] scannedItems = pcqMetaData.getScannableItems();
            if (scannedItems != null && scannedItems.length == 1
                && StringUtils.isNotEmpty(scannedItems[0].getOcrData())) {

                // Step 3. Retrieve and decode the Ocr Data.
                String ocrData = new String(Base64Utils.decodeFromString(scannedItems[0].getOcrData()));

                //Step 4. Get the payload object for the Json ocrData
                PcqPayLoad pcqPayLoad = new ObjectMapper().readValue(ocrData, PcqPayLoad.class);

                //Step 5. Perform the mapping and get the PcqAnswers object.
                PcqAnswerRequest answerRequest = performMapping(pcqMetaData, pcqPayLoad);
                log.info("Successfully completed mapping from payload to Answer Definition");
                return answerRequest;

            } else {
                log.error("No scanned items with ocr_data found in the meta-data file.");
            }

        } catch (JsonProcessingException jpe) {
            log.error("JsonProcessingException during payload parsing - " + jpe.getMessage());
        } catch (NumberFormatException nfe) {
            log.error("NumberFormatException during payload parsing - " + nfe.getMessage());
        }

        return null;
    }

    private PcqAnswerRequest performMapping(PcqMetaData metaData, PcqPayLoad payLoad) {
        PcqAnswerRequest pcqAnswerRequest = new PcqAnswerRequest();

        //Set the default values
        pcqAnswerRequest.setActor("UNKNOWN");
        pcqAnswerRequest.setChannel(2);
        pcqAnswerRequest.setCompletedDate(PcqUtils.getCurrentCompletedDate());
        pcqAnswerRequest.setDcnNumber(metaData.getOriginatingDcnNumber());
        pcqAnswerRequest.setPartyId("PaperForm");
        pcqAnswerRequest.setServiceId(metaData.getJurisdiction());
        pcqAnswerRequest.setVersionNo(1);
        pcqAnswerRequest.setPcqId(PcqUtils.generateUuid());
        pcqAnswerRequest.setFormId(metaData.getScannableItems()[0].getDocumentType());

        PcqAnswers answers = new PcqAnswers();
        PcqPayloadContents[] payloadContents = payLoad.getMetaDataContents();

        //For all the integer schema elements, set the values in the schema object first.
        /*for (String schemaElement : submitAnswerIntElements) {
            mapIntegerElements(payloadContents, answers, schemaElement);
        }*/
        mapIntegerFields(payloadContents,answers);

        //<element>_other field check
        mapOtherFields(payloadContents, answers);

        //Dob check and mapping
        if (payloadValidationHelper.isDobProvided(answers)) {
            mapDateOfBirth(payloadContents, answers);
        }

        //Validate and check disability_conditions
        payloadValidationHelper.validateDisabilityConditions(answers);

        //validate and check disability_impact
        payloadValidationHelper.validateDisabilityImpact(answers);

        //Validate and check disability_none.
        payloadValidationHelper.validateDisabilityNone(answers);

        //Validate and correct other fields.
        payloadValidationHelper.validateAndCorrectOtherFields(answers);

        //Validate and correct english language level.
        payloadValidationHelper.validateLanguageLevel(answers);

        pcqAnswerRequest.setPcqAnswers(answers);

        return pcqAnswerRequest;
    }

    /*@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void mapIntegerElements(PcqPayloadContents[] payloadContents, PcqAnswers answers, String schemaElement)
        throws IllegalAccessException, NoSuchFieldException {
        for (PcqPayloadContents payloadContent : payloadContents) {
            if (payloadContent.getFieldName().equals(schemaElement)) {
                // Find the field name of the element in the SubmitAnswers object.
                int index = ArrayUtils.indexOf(submitAnswerIntElements, schemaElement);
                String field = submitAnswerIntFields[index];
                Field schemaField = PcqAnswers.class.getDeclaredField(submitAnswerIntFields[index]);
                // Make the field accessible
                PcqUtils.makeFieldAccessible(schemaField);
                // If the element value has been already set in answers object i.e. more than one check-box
                // has been ticked by the user, then set the answer to "-1".
                if (schemaField.get(answers) == null) {
                    if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                        schemaField.set(answers, Integer.valueOf(payloadContent.getFieldValue()));
                    }
                } else {
                    log.error("Invalid answer for " + schemaElement + ", found more than one value in payload.");
                    schemaField.set(answers, -1);
                }
            }
        }
    }*/

    private void mapIntegerFields(PcqPayloadContents[] payloadContents, PcqAnswers answers) {
        for (PcqPayloadContents payloadContent : payloadContents) {
            switch (payloadContent.getFieldName()) {
                case "dob_provided" :
                    if (answers.getDobProvided() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDobProvided(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for dob_provided, found more than one value in payload.");
                        answers.setDobProvided(-1);
                    }
                    break;
                case "language_main" :
                    if (answers.getLanguageMain() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setLanguageMain(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for language_main, found more than one value in payload.");
                        answers.setLanguageMain(-1);
                    }
                    break;
                case "english_language_level" :
                    if (answers.getEnglishLanguageLevel() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setEnglishLanguageLevel(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for english_language_level, found more than one value in payload.");
                        answers.setEnglishLanguageLevel(-1);
                    }
                    break;
                case "sex" :
                    if (answers.getSex() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setSex(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for sex, found more than one value in payload.");
                        answers.setSex(-1);
                    }
                    break;
                case "gender_different" :
                    if (answers.getGenderDifferent() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setGenderDifferent(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for gender_different, found more than one value in payload.");
                        answers.setGenderDifferent(-1);
                    }
                    break;
                case "sexuality" :
                    if (answers.getSexuality() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setSexuality(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for sexuality, found more than one value in payload.");
                        answers.setSexuality(-1);
                    }
                    break;
                case "marriage" :
                    if (answers.getMarriage() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setMarriage(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for marriage, found more than one value in payload.");
                        answers.setMarriage(-1);
                    }
                    break;
                case "ethnicity" :
                    if (answers.getEthnicity() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setEthnicity(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for ethnicity, found more than one value in payload.");
                        answers.setEthnicity(-1);
                    }
                    break;
                case "religion" :
                    if (answers.getReligion() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setReligion(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for religion, found more than one value in payload.");
                        answers.setReligion(-1);
                    }
                    break;
                case "disability_condition" :
                    if (answers.getDisabilityConditions() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityConditions(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_condition, found more than one value in payload.");
                        answers.setDisabilityConditions(-1);
                    }
                    break;
                case "disability_impact" :
                    if (answers.getDisabilityImpact() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityImpact(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_impact, found more than one value in payload.");
                        answers.setDisabilityImpact(-1);
                    }
                    break;
                case "disability_vision" :
                    if (answers.getDisabilityVision() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityVision(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_vision, found more than one value in payload.");
                        answers.setDisabilityVision(-1);
                    }
                    break;
                case "disability_hearing" :
                    if (answers.getDisabilityHearing() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityHearing(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_hearing, found more than one value in payload.");
                        answers.setDisabilityHearing(-1);
                    }
                    break;
                case "disability_mobility" :
                    if (answers.getDisabilityMobility() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityMobility(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_mobility, found more than one value in payload.");
                        answers.setDisabilityMobility(-1);
                    }
                    break;
                case "disability_dexterity" :
                    if (answers.getDisabilityDexterity() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityDexterity(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_dexterity, found more than one value in payload.");
                        answers.setDisabilityDexterity(-1);
                    }
                    break;
                case "disability_learning" :
                    if (answers.getDisabilityLearning() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityLearning(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_learning, found more than one value in payload.");
                        answers.setDisabilityLearning(-1);
                    }
                    break;
                case "disability_memory" :
                    if (answers.getDisabilityMemory() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityMemory(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_memory, found more than one value in payload.");
                        answers.setDisabilityMemory(-1);
                    }
                    break;
                case "disability_mental_health" :
                    if (answers.getDisabilityMentalHealth() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityMentalHealth(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_mental_health, found more than one value in payload.");
                        answers.setDisabilityMentalHealth(-1);
                    }
                    break;
                case "disability_stamina" :
                    if (answers.getDisabilityStamina() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityStamina(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_stamina, found more than one value in payload.");
                        answers.setDisabilityStamina(-1);
                    }
                    break;
                case "disability_social" :
                    if (answers.getDisabilitySocial() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilitySocial(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_social, found more than one value in payload.");
                        answers.setDisabilitySocial(-1);
                    }
                    break;
                case "disability_other" :
                    if (answers.getDisabilityOther() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityOther(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_other, found more than one value in payload.");
                        answers.setDisabilityOther(-1);
                    }
                    break;
                case "disability_none" :
                    if (answers.getDisabilityNone() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setDisabilityNone(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for disability_none, found more than one value in payload.");
                        answers.setDisabilityNone(-1);
                    }
                    break;
                case "pregnancy" :
                    if (answers.getPregnancy() == null) {
                        if (!StringUtils.isEmpty(payloadContent.getFieldValue())) {
                            answers.setPregnancy(Integer.valueOf(payloadContent.getFieldValue()));
                        }
                    } else {
                        log.error("Invalid answer for pregnancy, found more than one value in payload.");
                        answers.setPregnancy(-1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.CyclomaticComplexity"})
    private void mapOtherFields(PcqPayloadContents[] payloadContents, PcqAnswers answers) {
        for (PcqPayloadContents payloadContent : payloadContents) {
            switch (payloadContent.getFieldName()) {
                case "language_other" :
                    answers.setLanguageOther(nullIfEmpty(payloadContent.getFieldValue()));
                    break;
                case "other_religion_text" :
                    answers.setReligionOther(nullIfEmpty(payloadContent.getFieldValue()));
                    break;
                case "other_white_ethnicity_text" :
                case "other_mixed_ethnicity_text" :
                case "other_asian_ethnicity_text" :
                case "other_african_caribbean_ethnicity_text" :
                case "other_ethnicity_text" :
                    setEthnicity(payloadContent, answers);
                    break;
                case "other_disability_details" :
                    answers.setDisabilityConditionOther(nullIfEmpty(payloadContent.getFieldValue()));
                    break;
                case "other_sexuality_text" :
                    answers.setSexualityOther(nullIfEmpty(payloadContent.getFieldValue()));
                    break;
                case "gender_different_text" :
                    answers.setGenderOther(nullIfEmpty(payloadContent.getFieldValue()));
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void mapDateOfBirth(PcqPayloadContents[] payloadContents, PcqAnswers answers) {
        String dobDay = "";
        String dobMonth = "";
        String dobYear = "";
        for (PcqPayloadContents payloadContent : payloadContents) {
            switch (payloadContent.getFieldName()) {
                case "dob_day" :
                    dobDay = payloadContent.getFieldValue();
                    break;
                case "dob_month" :
                    dobMonth = payloadContent.getFieldValue();
                    break;
                case "dob_year" :
                    dobYear = payloadContent.getFieldValue();
                    break;
                default:
                    break;
            }
        }

        //Prefix the month and day with 0 if the length is 1 and numeric.
        dobMonth = PcqUtils.formatDobField(dobMonth);
        dobDay = PcqUtils.formatDobField(dobDay);

        String dob = dobYear + "-" + dobMonth + "-" + dobDay;

        if (PcqUtils.isDobValid(dob)) {
            answers.setDobProvided(1);
            answers.setDob(PcqUtils.generateCompleteDobString(dob));
        } else {
            answers.setDob(null);
            log.error("Invalid Dob provided - " + dob + ", setting the Dob to Null");
        }

    }


    private void setEthnicity(PcqPayloadContents payloadContent, PcqAnswers answers) {
        if (answers.getEthnicityOther() == null && answers.getEthnicity() != null && answers.getEthnicity() != -1) {
            // Only set the value if ethnicity is not already set.
            answers.setEthnicityOther(nullIfEmpty(payloadContent.getFieldValue()));
        } else {
            if (!EMPTY_STRING.equals(payloadContent.getFieldValue())) {
                // Invalid answer supplied as ethnicity is already set in the payload.
                log.error("Invalid answer for " + payloadContent.getFieldName() + ", found more than one "
                              + "other value in payload.");
                answers.setEthnicityOther(null);
                answers.setEthnicity(-1);
            }
        }
    }

}

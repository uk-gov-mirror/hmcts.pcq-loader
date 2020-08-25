package uk.gov.hmcts.reform.pcqloader.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.model.PcqMetaData;
import uk.gov.hmcts.reform.pcqloader.model.PcqPayLoad;
import uk.gov.hmcts.reform.pcqloader.model.PcqPayloadContents;
import uk.gov.hmcts.reform.pcqloader.model.PcqScannableItems;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

import java.lang.reflect.Field;

@Component
@Slf4j
public class PayloadMappingHelper {

    private final String[] submitAnswerIntElements = {"dob_provided", "language_main", "english_language_level", "sex",
        "gender_different", "sexuality", "marriage", "ethnicity", "religion", "disability_conditions",
        "disability_impact", "disability_vision", "disability_hearing", "disability_mobility", "disability_dexterity",
        "disability_learning", "disability_memory", "disability_mental_health", "disability_stamina",
        "disability_social", "disability_other", "disability_none", "pregnancy"};

    public PcqAnswerRequest mapPayLoadToPcqAnswers(String dcnNumber, String metaDataString) {

        try {
            // Step 1. Convert the JSon String into an Java Object
            PcqMetaData pcqMetaData = new ObjectMapper().readValue(metaDataString, PcqMetaData.class);

            // Step 2. Retrieve the Scannable items
            PcqScannableItems[] scannedItems = pcqMetaData.getScannableItems();
            if (scannedItems != null && scannedItems.length > 0) {

                // Step 3. Retrieve and decode the Ocr Data.
                String ocrData = new String(Base64Utils.decodeFromString(scannedItems[0].getOcrData()));

                //Step 4. Get the payload object for the Json ocrData
                PcqPayLoad pcqPayLoad = new ObjectMapper().readValue(ocrData, PcqPayLoad.class);

                //Step 5. Perform the mapping and get the PcqAnswers object.
                PcqAnswerRequest answerRequest = performMapping(pcqMetaData, pcqPayLoad, dcnNumber);
                log.info("Successfully completed mapping from payload to Answer Definition");
                return answerRequest;

            } else {
                log.error("No scanned items found in the meta-data file.");
            }

        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException during payload parsing - " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException during payload parsing - " + e.getMessage());
        }

        return null;
    }

    private PcqAnswerRequest performMapping(PcqMetaData metaData, PcqPayLoad payLoad, String dcnNumber)
        throws IllegalAccessException {
        PcqAnswerRequest pcqAnswerRequest = new PcqAnswerRequest();

        //Set the default values
        pcqAnswerRequest.setActor("UNKNOWN");
        pcqAnswerRequest.setChannel(2);
        pcqAnswerRequest.setCompletedDate(PcqLoaderUtils.getCurrentCompletedDate());
        pcqAnswerRequest.setDcnNumber(dcnNumber);
        pcqAnswerRequest.setPartyId("PaperForm");
        pcqAnswerRequest.setServiceId(metaData.getJurisdiction());
        pcqAnswerRequest.setVersionNo(1);
        pcqAnswerRequest.setPcqId(PcqLoaderUtils.generateUuid());
        pcqAnswerRequest.setFormId(metaData.getScannableItems()[0].getDocumentType());

        PcqAnswers answers = new PcqAnswers();
        PcqPayloadContents[] payloadContents = payLoad.getMetaDataContents();

        //For all the integer schema elements, set the values in the schema object first.
        mapIntegerElements(payloadContents, answers);

        //<element>_other field check
        mapOtherFields(payloadContents, answers);

        //Dob check and mapping
        mapDateOfBirth(payloadContents, answers);

        //Validate and check disability_none.
        validateDisabilityNone(answers);

        pcqAnswerRequest.setPcqAnswers(answers);

        return pcqAnswerRequest;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private void mapIntegerElements(PcqPayloadContents[] payloadContents, PcqAnswers answers)
        throws IllegalAccessException {
        for (String schemaElement : submitAnswerIntElements) {

            for (PcqPayloadContents payloadContent : payloadContents) {
                if (payloadContent.getFieldName().equals(schemaElement)) {
                    // Find the field name of the element in the SubmitAnswers object.
                    Field schemaField = ReflectionUtils.findField(PcqAnswers.class, schemaElement);
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
        }
    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.CyclomaticComplexity"})
    private void mapOtherFields(PcqPayloadContents[] payloadContents, PcqAnswers answers) {
        for (PcqPayloadContents payloadContent : payloadContents) {
            switch (payloadContent.getFieldName()) {
                case "language_other" :
                    answers.setLanguageOther(payloadContent.getFieldValue());
                    break;
                case "other_religion_text" :
                    answers.setReligionOther(payloadContent.getFieldValue());
                    break;
                case "other_white_ethnicity_text" :
                case "other_mixed_ethnicity_text" :
                case "other_asian_ethnicity_text" :
                case "other_african_caribbean_ethnicity_text" :
                case "other_ethnicity_text" :
                    setEthnicity(payloadContent, answers);
                    break;
                case "other_disability_details" :
                    answers.setDisabilityConditionOther(payloadContent.getFieldValue());
                    break;
                case "other_sexuality_text" :
                    answers.setSexualityOther(payloadContent.getFieldValue());
                    break;
                case "gender_different_text" :
                    answers.setGenderOther(payloadContent.getFieldValue());
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
        if (!StringUtils.isEmpty(dobMonth) && dobMonth.length() == 1) {
            dobMonth = "0" + dobMonth;
        }

        if (!StringUtils.isEmpty(dobDay) && dobDay.length() == 1) {
            dobDay = "0" + dobDay;
        }

        String dob = dobYear + "-" + dobMonth + "-" + dobDay;

        if (PcqLoaderUtils.isDobValid(dob)) {
            answers.setDobProvided(1);
            answers.setDob(PcqLoaderUtils.generateCompleteDobString(dob));
        } else {
            answers.setDobProvided(0);
            answers.setDob(null);
            log.error("Invalid Dob provided - " + dob + ", setting the Dob to Null");
        }

    }

    private void validateDisabilityNone(PcqAnswers answers) {
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
        }
    }

    private void setEthnicity(PcqPayloadContents payloadContent, PcqAnswers answers) {
        if (answers.getEthnicityOther() == null && answers.getEthnicity() != -1) {
            // Only set the value if ethnicity is not already set.
            answers.setEthnicityOther(payloadContent.getFieldValue());
        } else {
            // Invalid answer supplied as ethnicity is already set in the payload.
            log.error("Invalid answer for " + payloadContent.getFieldName() + ", found more than one "
                          + "other value in payload.");
            answers.setEthnicityOther(null);
            answers.setEthnicity(-1);
        }
    }
}

package uk.gov.hmcts.reform.pcqloader.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import uk.gov.hmcts.reform.pcqloader.config.TestSupportUtils;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswers;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.model.PcqMetaData;
import uk.gov.hmcts.reform.pcqloader.model.PcqPayLoad;
import uk.gov.hmcts.reform.pcqloader.model.PcqPayloadContents;
import uk.gov.hmcts.reform.pcqloader.model.PcqScannableItems;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class PayloadMappingHelperTest {

    private static String EXPECTED_PARTY_ID = "PaperForm";
    private static String EXPECTED_ACTOR = "UNKNOWN";
    private static int EXPECTED_CHANNEL = 2;
    private static int EXPECTED_VERSION = 1;

    @InjectMocks
    private PayloadMappingHelper payloadMappingHelper;

    @Test
    public void mapPayLoadSuccess() throws IOException {
        String dcnNumber = "11003402";
        String metaDataPayLoad = jsonStringFromFile("testPayloadFiles/successMetaFile.json");

        PcqAnswerRequest mappedAnswers = payloadMappingHelper.mapPayLoadToPcqAnswers(dcnNumber, metaDataPayLoad);

        PcqPayLoad pcqPayload = (PcqPayLoad) jsonObjectFromString(TestSupportUtils.SUCCESS_PAYLOAD, PcqPayLoad.class);
        PcqMetaData metaData = (PcqMetaData) jsonObjectFromString(metaDataPayLoad, PcqMetaData.class);

        assertMapping(mappedAnswers, pcqPayload, metaData, dcnNumber);
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

    public static Object jsonObjectFromString(String jsonString, Class clazz) throws IOException {
        return new ObjectMapper().readValue(jsonString, clazz);
    }

    private void assertMapping(PcqAnswerRequest mappedAnswers, PcqPayLoad pcqPayLoad,
                                            PcqMetaData pcqMetaData, String dcnNumber) {
        //Check the primary key generated field is not missing.
        assertNotNull(mappedAnswers.getPcqId(), "PCQ Id is Null");

        //Check the correct DCN Number is populated.
        assertEquals(dcnNumber, mappedAnswers.getDcnNumber(), "DCN Number is not correct.");

        //Check the default values are set correctly for paper channel
        assertNull(mappedAnswers.getCaseId(), "Case Id is not correct.");
        assertEquals(EXPECTED_PARTY_ID, mappedAnswers.getPartyId(), "Party Id is not correct.");
        assertEquals(EXPECTED_CHANNEL, mappedAnswers.getChannel(), "Channel is not correct.");
        assertNotNull(mappedAnswers.getCompletedDate(), "Completed Date is empty");
        assertEquals(EXPECTED_ACTOR, mappedAnswers.getActor(), "Actor is not correct.");
        assertEquals(EXPECTED_VERSION, mappedAnswers.getVersionNo(), "Version Number is not correct.");
        assertNull(mappedAnswers.getOptOut(), "Opt Out is not correct.");


        //Check the information that matches the meta-data information.
        PcqScannableItems[] pcqScannedItems = pcqMetaData.getScannableItems();
        assertEquals(pcqScannedItems[0].getDocumentType(), mappedAnswers.getFormId(), "Form Id is not correct.");
        assertEquals(pcqMetaData.getJurisdiction(), mappedAnswers.getServiceId(), "Service Id is not correct.");

        //Check the answers matches the payload supplied.
        PcqPayloadContents[] payloadContents = pcqPayLoad.getMetaDataContents();
        PcqAnswers answers = mappedAnswers.getPcqAnswers();

        StringBuilder dob = new StringBuilder();
        String expectedLanguageMain = "";
        String languageOther = "";

        for (int i = 0; i < payloadContents.length; i++) {
            PcqPayloadContents payloadContent = payloadContents[i];
            switch (payloadContent.getFieldName()) {
                case "language_main" :
                    expectedLanguageMain = payloadContent.getFieldValue();
                    break;
                case "language_other" :
                    languageOther = payloadContent.getFieldValue();
                    break;
                case "english_language_level" :
                    assertCustomEquals(payloadContent.getFieldValue(), answers.getEnglishLanguageLevel(),
                                       "English_Language_Level is not correct.");
                    break;
                case "religion" :
                    assertCustomEquals(payloadContent.getFieldValue(), answers.getReligion(),
                                       "Religion is not correct.");
                    break;
                case "other_religion_text" :
                    assertEquals(payloadContent.getFieldValue(), answers.getReligionOther(),
                                 "Other Religion is not correct.");
                    break;
                case "dob_day" :
                    dob = new StringBuilder(StringUtils.defaultString(payloadContent.getFieldValue()));
                    break;
                case "dob_month" :
                case "dob_year" :
                    dob.insert(0, StringUtils.defaultString(payloadContent.getFieldValue()) + "_");
                    break;
                case "dob_provided" :
                    assertCustomEquals(payloadContent.getFieldValue(), answers.getDobProvided(),
                                       "Dob Provided is not correct.");
                    break;
                default :
                    break;
            }
        }

    }

    private void checkLanguageMain(String expected, String expectedOther, String actual, String actualOther) {

    }

    private void assertCustomEquals(String expected, Integer actual, String message) {
        if (actual == null) {
            assertEquals(expected, null, message);
        } else {
            assertEquals(expected, String.valueOf(actual), message);
        }
    }



}

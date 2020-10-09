package uk.gov.hmcts.reform.pcqloader.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("PMD.TooManyMethods")
class PcqLoaderUtilsTest {

    private static final String DOB_VALIDATION_MSG = "Dob validation should not return true";

    @Test
    void testExtractDcnNumberSuccess() {
        String testFileName = "1789034567_01-01-1900-12-00-00.zip";

        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(testFileName);

        assertEquals("1789034567", extractedDcnNumber, "DCN Number Different.");
    }

    @Test
    void testExtractDcnNumberEmpty() {
        String testFileName = "";

        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(testFileName);

        assertNull(extractedDcnNumber, "DCN Number Different.");
    }

    @Test
    void testExtractDcnNumberNull() {
        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(null);

        assertNull(extractedDcnNumber, "DCN Number Different.");
    }

    @Test
    void testGetCurrentCompletedDate() {
        Pattern pattern = Pattern.compile("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d(?:\\.\\d+)?Z?");
        Matcher matcher = pattern.matcher(PcqLoaderUtils.getCurrentCompletedDate());

        assertTrue(matcher.matches(), "Completed Date is in wrong format");
    }

    @Test
    void testGenerateUuidNotNull() {
        assertNotNull(PcqLoaderUtils.generateUuid(), "Uuid is null");
    }

    @Test
    void testGenerateUuidRandom() {
        String pcqIdOne = PcqLoaderUtils.generateUuid();
        String pcqIdSecond = PcqLoaderUtils.generateUuid();

        assertNotEquals(pcqIdOne, pcqIdSecond, "PcqIds are not random");
    }

    @Test
    void invalidDobCharacters() {
        // User has not supplied any dob data. So the day, month and year will be empty strings.
        String invalidDob = "--";
        boolean isDobValid = PcqLoaderUtils.isDobValid(invalidDob);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        // User supplied invalid characters in the day field.
        String invalidDob2 = "--Abba";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDob2);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        // User supplied invalid characters in the month field.
        String invalidDob3 = "-asdsd-";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDob3);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        // User supplied invalid characters in the year field.
        String invalidDob4 = "ipip--01";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDob4);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        // User supplied invalid characters in the all fields.
        String invalidDob5 = "ipip-asdsd-Abba";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDob5);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);
    }

    @Test
    void invalidDobDay() {
        //User has supplied an numeric but invalid day field
        String invalidDobDay = "1900-01-00";
        boolean isDobValid = PcqLoaderUtils.isDobValid(invalidDobDay);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        String invalidDobDay2 = "1900-01-32";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDobDay2);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        String invalidDobDay3 = "1900-02-30";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDobDay3);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);
    }

    @Test
    void invalidDobMonth() {
        //User has supplied an numeric but invalid month field
        String invalidDobMonth = "1900-00-01";
        boolean isDobValid = PcqLoaderUtils.isDobValid(invalidDobMonth);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        String invalidDobMonth2 = "1900-13-30";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDobMonth2);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);
    }

    @Test
    void invalidDobYear() {
        //User has supplied an numeric but invalid year field
        String invalidDobYear = "01-01-01";
        boolean isDobValid = PcqLoaderUtils.isDobValid(invalidDobYear);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);

        String invalidDobYear2 = "21234-12-30";
        isDobValid = PcqLoaderUtils.isDobValid(invalidDobYear2);
        assertFalse(isDobValid, DOB_VALIDATION_MSG);
    }

    @Test
    void dobValidation() {
        //User has supplied an numeric and valid dob data
        String validDob = "2001-01-31";
        boolean isDobValid = PcqLoaderUtils.isDobValid(validDob);
        assertTrue(isDobValid, "Dob validation should not return false");

        String validDob2 = "2000-02-29";
        isDobValid = PcqLoaderUtils.isDobValid(validDob2);
        assertTrue(isDobValid, "Dob validation should not return false");

        String validDob3 = "1951-06-30";
        isDobValid = PcqLoaderUtils.isDobValid(validDob3);
        assertTrue(isDobValid, "Dob validation should not return false");
    }

    @Test
    void testCompleteDobString() {
        Pattern pattern = Pattern.compile("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d(?:\\.\\d+)?Z?");
        String dobPartial = "1900-01-01";
        String completeDob = PcqLoaderUtils.generateCompleteDobString(dobPartial);
        Matcher matcher = pattern.matcher(completeDob);
        assertTrue(matcher.matches(), "Dob string is not valid.");
    }

    @Test
    void testAuthorisationToken() {
        String token = PcqLoaderUtils.generateAuthorizationToken("Test", "TestSubject");
        assertNotNull(token, "Authorisation token is null");
    }

}

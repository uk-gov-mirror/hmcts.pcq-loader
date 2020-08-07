package uk.gov.hmcts.reform.pcqloader.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PcqLoaderUtilsTest {

    @Test
    public void testExtractDcnNumberSuccess() {
        String testFileName = "1789034567_01-01-1900-12-00-00.zip";

        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(testFileName);

        assertEquals("1789034567", extractedDcnNumber, "DCN Number Different.");
    }

    @Test
    public void testExtractDcnNumberEmpty() {
        String testFileName = "";

        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(testFileName);

        assertNull(extractedDcnNumber, "DCN Number Different.");
    }

    @Test
    public void testExtractDcnNumberNull() {
        String testFileName = null;

        String extractedDcnNumber = PcqLoaderUtils.extractDcnNumberFromFile(testFileName);

        assertNull(extractedDcnNumber, "DCN Number Different.");
    }
}

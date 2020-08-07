package uk.gov.hmcts.reform.pcqloader.utils;

import org.springframework.util.StringUtils;

public final class PcqLoaderUtils {

    private static final int START_INDEX = 0;
    private static final String SEPARATOR = "_";

    private PcqLoaderUtils() {

    }

    public static String extractDcnNumberFromFile(String fileName) {
        //Based on the Exela JSON schema, the zip file name format will be unqiueId_datetimecreated.zip where
        //the datetimecreated will be of format - DD-MM-YYYY-HH-MM-SS.
        //The DCN Number will be the uniqueId that needs to be extracted.
        if (!StringUtils.isEmpty(fileName)) {
            return fileName.substring(START_INDEX, fileName.indexOf(SEPARATOR));
        }

        return null;
    }

}

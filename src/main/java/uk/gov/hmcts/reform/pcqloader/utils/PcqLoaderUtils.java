package uk.gov.hmcts.reform.pcqloader.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class PcqLoaderUtils {

    private static final int START_INDEX = 0;
    private static final String SEPARATOR = "_";
    private static final String COMPLETED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DOB_REGEX = "\\d{4}-[01]\\d-[0-3]\\d";
    private static final String DOB_TIME_CONSTANT = "'T'HH:mm:ss.SSS'Z'";

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

    public static String getCurrentCompletedDate() {
        Timestamp completedTime = Timestamp.valueOf(LocalDateTime.now());
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPLETED_DATE_FORMAT, Locale.UK);
        return dateFormat.format(completedTime);
    }

    public static String generateUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static boolean isDobValid(String suppliedDob) {
        // Step 1 - Check the format is correct.
        Pattern pattern = Pattern.compile(DOB_REGEX);
        Matcher matcher = pattern.matcher(suppliedDob);
        if (matcher.matches()) {
            // Step 2 - Convert to Date object and confirm it is correct
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
            try {
                dateFormat.parse(suppliedDob);
                return true;
            } catch (ParseException e) {
                log.error("Dob supplied is invalid - " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    public static String generateCompleteDobString(String suppliedDob) {
        return suppliedDob + DOB_TIME_CONSTANT;
    }

}

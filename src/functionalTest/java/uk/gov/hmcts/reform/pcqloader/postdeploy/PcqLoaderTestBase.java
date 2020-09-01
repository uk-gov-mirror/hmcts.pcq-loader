package uk.gov.hmcts.reform.pcqloader.postdeploy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
public class PcqLoaderTestBase {

    private static final String COMPLETED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Obtains a JSON String from a JSON file in the classpath (Resources directory).
     * @param fileName - The name of the Json file from classpath.
     * @return - JSON String from the file.
     * @throws IOException - If there is any issue when reading from the file.
     */
    protected String jsonStringFromFile(String fileName) throws IOException {
        File resource = new ClassPathResource(fileName).getFile();
        return new String(Files.readAllBytes(resource.toPath()));
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod"})
    private WebClient createPcqBackendWebClient(String apiUrl, String secretKey) {
        return WebClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Correlation-Id", "Pcq Consolidation Functional Test")
                .defaultHeader("Authorization", "Bearer " + generateTestToken(secretKey))
                .defaultUriVariables(Collections.singletonMap("url", apiUrl))
                .build();
    }

    private String generateTestToken(String secretKey) {
        List<String> authorities = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        authorities.add("TEST_AUTHORITY");

        return Jwts.builder()
                .setSubject("TEST")
                .claim("authorities", authorities)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + 500_000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    @SuppressWarnings({"PMD.UnusedPrivateMethod"})
    private String updateCompletedDate(String completedDateStr) {
        Timestamp completedTime = getTimeFromString(completedDateStr);
        Calendar calendar = Calendar.getInstance();
        completedTime.setTime(calendar.getTimeInMillis());
        return convertTimeStampToString(completedTime);
    }

    private Timestamp getTimeFromString(String timeStampStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(COMPLETED_DATE_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(timeStampStr));

        return Timestamp.valueOf(localDateTime);
    }

    private String convertTimeStampToString(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPLETED_DATE_FORMAT, Locale.UK);
        return dateFormat.format(timestamp);
    }
}

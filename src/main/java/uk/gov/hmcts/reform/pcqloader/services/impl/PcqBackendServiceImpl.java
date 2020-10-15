package uk.gov.hmcts.reform.pcqloader.services.impl;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pcqloader.controller.feign.PcqBackendFeignClient;
import uk.gov.hmcts.reform.pcqloader.exceptions.ExternalApiException;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;
import uk.gov.hmcts.reform.pcqloader.services.PcqBackendService;
import uk.gov.hmcts.reform.pcqloader.utils.JsonFeignResponseUtil;
import uk.gov.hmcts.reform.pcqloader.utils.PcqLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class PcqBackendServiceImpl implements PcqBackendService {

    private final PcqBackendFeignClient pcqBackendFeignClient;

    @Value("${coRelationId:Test}")
    private String coRelationHeader;

    @Value("${security.jwt.secret:JwtSecretKey}")
    private String jwtSecretKey;

    @Autowired
    public PcqBackendServiceImpl(PcqBackendFeignClient pcqBackendFeignClient) {
        this.pcqBackendFeignClient = pcqBackendFeignClient;
    }


    @Override
    @SuppressWarnings({"unchecked", "PMD.PreserveStackTrace"})
    public ResponseEntity<Map<String, String>> submitAnswers(PcqAnswerRequest answerRequest) {
        ResponseEntity<Map<String, String>> responseEntity;

        //Generate the Bearer JWT token.
        String jwtToken = "Bearer " + PcqLoaderUtils.generateAuthorizationToken(jwtSecretKey,
                                                                                answerRequest.getPartyId());

        //Invoke the API
        try (Response response = pcqBackendFeignClient.submitAnswers(coRelationHeader + answerRequest.getDcnNumber(),
                                                                     jwtToken, answerRequest)) {
            responseEntity = JsonFeignResponseUtil.toResponseEntity(response, HashMap.class);
        } catch (FeignException ex) {
            throw new ExternalApiException(HttpStatus.valueOf(ex.status()), ex.getMessage());
        } catch (IOException | IllegalArgumentException ioe) {
            throw new ExternalApiException(HttpStatus.SERVICE_UNAVAILABLE, ioe.getMessage());
        }

        return responseEntity;
    }
}

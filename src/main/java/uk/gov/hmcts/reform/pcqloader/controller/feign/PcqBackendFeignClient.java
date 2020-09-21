package uk.gov.hmcts.reform.pcqloader.controller.feign;

import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.hmcts.reform.pcqloader.config.FeignInterceptorConfiguration;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;

@FeignClient(name = "PcqBackendFeignClient", url = "${pcqBackendUrl}", configuration =
        FeignInterceptorConfiguration.class)
public interface PcqBackendFeignClient {


    @PostMapping("/pcq/backend/submitAnswers")
    @Headers("Content-Type: application/json")
    Response submitAnswers(@RequestHeader("X-Correlation-Id") String token,
                           @RequestHeader("Authorization") String bearerToken,
                           PcqAnswerRequest answerRequest);

}

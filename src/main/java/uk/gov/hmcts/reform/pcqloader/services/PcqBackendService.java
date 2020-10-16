package uk.gov.hmcts.reform.pcqloader.services;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.pcq.commons.model.PcqAnswerRequest;

import java.util.Map;

public interface PcqBackendService {

    ResponseEntity<Map<String, String>> submitAnswers(PcqAnswerRequest answerRequest);

}

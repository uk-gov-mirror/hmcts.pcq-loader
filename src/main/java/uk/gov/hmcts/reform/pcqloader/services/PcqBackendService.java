package uk.gov.hmcts.reform.pcqloader.services;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.pcqloader.model.PcqAnswerRequest;

public interface PcqBackendService {

    ResponseEntity submitAnswers(PcqAnswerRequest answerRequest);

}

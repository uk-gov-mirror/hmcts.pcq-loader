package uk.gov.hmcts.reform.pcqloader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PcqScannableItems implements Serializable {
    public static final long serialVersionUID = 45589453L;

    @JsonProperty("document_control_number")
    private String documentControlNumber;

    @JsonProperty("document_type")
    private String documentType;

    @JsonProperty("scanning_date")
    private String scanningDate;

    @JsonProperty("ocr_accuracy")
    private String ocrAccuracy;

    @JsonProperty("ocr_data")
    private String ocrData;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("notes")
    private String notes;
}

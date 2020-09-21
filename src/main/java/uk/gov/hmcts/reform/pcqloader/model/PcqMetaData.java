package uk.gov.hmcts.reform.pcqloader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PcqMetaData implements Serializable {
    public static final long serialVersionUID = 43289453L;

    @JsonProperty("po_box")
    private String poBox;

    @JsonProperty("jurisdiction")
    private String jurisdiction;

    @JsonProperty("delivery_date")
    private String deliveryDate;

    @JsonProperty("opening_date")
    private String openingDate;

    @JsonProperty("zip_file_createddate")
    private String zipFileCreatedDate;

    @JsonProperty("zip_file_name")
    private String zipFileName;

    @JsonProperty("originating_document_control_number")
    private String originatingDcnNumber;

    @JsonProperty("scannable_items")
    private PcqScannableItems[] scannableItems;

}

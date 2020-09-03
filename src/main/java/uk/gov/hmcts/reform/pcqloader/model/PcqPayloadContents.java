package uk.gov.hmcts.reform.pcqloader.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PcqPayloadContents implements Serializable {
    public static final long serialVersionUID = 7328743L;

    @JsonProperty("metadata_field_name")
    private String fieldName;

    @JsonProperty("metadata_field_value")
    private String fieldValue;
}

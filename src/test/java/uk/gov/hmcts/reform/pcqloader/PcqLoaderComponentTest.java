package uk.gov.hmcts.reform.pcqloader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PcqLoaderComponentTest {

    @InjectMocks
    private PcqLoaderComponent pcqLoaderComponent;

    @Test
    public void executeSuccess() {
        pcqLoaderComponent.execute();
    }
}

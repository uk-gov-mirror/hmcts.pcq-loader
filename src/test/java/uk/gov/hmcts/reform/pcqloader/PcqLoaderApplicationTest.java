package uk.gov.hmcts.reform.pcqloader;


import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PcqLoaderApplicationTest {

    @InjectMocks
    private PcqLoaderApplication testPcqLoaderApplication;

    @Mock
    private TelemetryClient client;

    @Mock
    private PcqLoaderComponent pcqLoaderComponent;

    @Test
    public void testApplicationExecuted() throws Exception {
        testPcqLoaderApplication.run(null);
        verify(pcqLoaderComponent, times(1)).execute();
        verify(client, times(1)).flush();
    }

}

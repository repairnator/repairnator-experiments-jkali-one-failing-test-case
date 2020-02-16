/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.yahoo.presto.audit;

import com.facebook.presto.spi.ErrorCode;
import com.facebook.presto.spi.ErrorType;
import com.facebook.presto.spi.eventlistener.QueryCompletedEvent;
import com.facebook.presto.spi.eventlistener.QueryContext;
import com.facebook.presto.spi.eventlistener.QueryFailureInfo;
import com.facebook.presto.spi.eventlistener.QueryIOMetadata;
import com.facebook.presto.spi.eventlistener.QueryInputMetadata;
import com.facebook.presto.spi.eventlistener.QueryMetadata;
import com.facebook.presto.spi.eventlistener.QueryStatistics;
import com.facebook.presto.spi.eventlistener.StageCpuDistribution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Test(singleThreaded = true)
public class TestAuditLogListener
{
    private URI uri;
    private QueryStatistics statistics;
    private QueryContext context;
    private QueryIOMetadata ioMetadata;
    private Instant createTime;
    private Instant executionStartTime;
    private Instant endTime;
    private AuditLogListener listener;
    private Gson gson;

    @BeforeClass
    public void setUp()
            throws Exception
    {
        uri = new URI("http://example.com:8080/v1/query/20170521_140224_00002_gd5k3");
        statistics = new QueryStatistics(Duration.ofMillis(100),
                Duration.ofMillis(200),
                Duration.ofMillis(300),
                (Optional<Duration>) Optional.of(Duration.ofMillis(400)),
                (Optional<Duration>) Optional.of(Duration.ofMillis(500)),
                10001,
                10002,
                10003,
                14,
                0,
                0, 0,
                2048.0,
                4096,
                true,
                new ArrayList<StageCpuDistribution>() {},
                new ArrayList<String>()
                {{
                    add("operatorSummaries 01");
                }}
        );
        context = new QueryContext(
                "test-user",
                (Optional<String>) Optional.of("principal"),
                (Optional<String>) Optional.of("example.com"),
                (Optional<String>) Optional.of("StatementClient 0.167"),
                (Optional<String>) Optional.of("clientInfo"),
                (Optional<String>) Optional.of("presto-cli"),
                (Optional<String>) Optional.of("catalog"),
                (Optional<String>) Optional.of("schema"),
                (Optional<String>) Optional.of(""),
                new HashMap<String, String>(),
                "127.0.0.1",
                "0.175",
                "environment");
//        ZoneId jstt_zone = ZoneId.of("Asia/Tokyo");
        ZoneId jstt_zone = ZoneId.of("America/Los_Angeles");
        ioMetadata = new QueryIOMetadata(new ArrayList<QueryInputMetadata>(),
                Optional.empty());
        createTime = ZonedDateTime.of(2017, 6 + 1, 15, 10, 0, 0, 0, jstt_zone).toInstant();
        executionStartTime = ZonedDateTime.of(2017, 6 + 1, 15, 10, 0, 1, 0, jstt_zone).toInstant();
        endTime = ZonedDateTime.of(2017, 6 + 1, 15, 10, 0, 3, 0, jstt_zone).toInstant();

        Map<String, String> requiredConfig = new HashMap<String, String>();
        requiredConfig.put("event-listener.audit-log-path", "/test/path");
        requiredConfig.put("event-listener.audit-log-filename", "test-filename.log");
        listener = new AuditLogListener(requiredConfig);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Test
    public void testBuildAuditRecordNormal()
            throws URISyntaxException
    {
        QueryMetadata metadata = new QueryMetadata("20170606_044544_00024_nfhe3",
                Optional.of("4c52973c-14c6-4534-837f-238e21d9b061"),
                "select * from airdelays_s3_csv WHERE kw = 'presto-kw-example' limit 5",
                "FINISHED",
                uri,
                Optional.empty(),
                Optional.empty());
        QueryCompletedEvent queryCompletedEvent = new QueryCompletedEvent(metadata,
                statistics,
                context,
                ioMetadata,
                Optional.empty(),
                createTime,
                executionStartTime,
                endTime);

        AuditRecord record = listener.buildAuditRecord(queryCompletedEvent);

        assertThat(gson.toJson(record))
                .contains("\"queryId\":\"20170606_044544_00024_nfhe3\"")
                .contains("\"query\":\"select * from airdelays_s3_csv WHERE kw = 'presto-kw-example' limit 5\"")
                .contains("\"userAgent\":\"StatementClient 0.167\"")
                .contains("\"source\":\"presto-cli\"}")
                .contains("\"errorCode\":0")
                .contains("\"createTime\":\"20170715100000.000\"")
                .contains("\"executionStartTime\":\"20170715100001.000\"")
                .contains("\"endTime\":\"20170715100003.000\"")
                .contains("\"createTimestamp\":1.5000804E9")
                .contains("\"executionStartTimestamp\":1.500080401E9")
                .contains("\"endTimestamp\":1.500080403E9")
                .doesNotContain("\"failureMessage\"")
                .doesNotContain("\"errorName\"")
        ;
    }

    @Test
    public void testBuildAuditRecordFailure()
            throws URISyntaxException
    {
        QueryMetadata metadata = new QueryMetadata("20170606_044544_00024_nfhe3",
                Optional.of("4c52973c-14c6-4534-837f-238e21d9b061"),
                "select 2a",
                "FAILED",
                uri,
                Optional.empty(),
                Optional.empty());
        ErrorCode errorCode = new ErrorCode(1, "SYNTAX_ERROR", ErrorType.USER_ERROR);
        QueryFailureInfo failureInfo = new QueryFailureInfo(errorCode,
                Optional.of("com.facebook.presto.sql.parser.ParsingException"),
                Optional.of("line 1:15: mismatched input '0' expecting ')'"),
                Optional.empty(),
                Optional.empty(),
                "{json-error}"
        );
        QueryCompletedEvent queryCompletedEvent = new QueryCompletedEvent(metadata,
                statistics,
                context,
                ioMetadata,
                Optional.of(failureInfo),
                createTime,
                executionStartTime,
                endTime);

        AuditRecord record = listener.buildAuditRecord(queryCompletedEvent);
        assertThat(gson.toJson(record))
                .contains("\"errorCode\":1")
                .contains("\"errorName\":\"SYNTAX_ERROR\"")
                .contains("\"failureMessage\":\"line 1:15: mismatched input '0' expecting ')'\"")
                .contains("\"failureType\":\"com.facebook.presto.sql.parser.ParsingException\"");
    }
}

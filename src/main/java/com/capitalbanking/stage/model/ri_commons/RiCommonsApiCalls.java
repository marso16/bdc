package com.capitalbanking.stage.model.ri_commons;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "ri_commons_api_calls")
public class RiCommonsApiCalls {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ri_commons_api_calls_seq")
    @SequenceGenerator(name = "ri_commons_api_calls_seq", sequenceName = "ri_commons_api_calls_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "trans_id", length = 100)
    private String transId;

    @Column(name = "type_operation", length = 50)
    private String typeOperation;

    @Lob
    @Column(name = "request_json")
    private String requestJson;

    @Lob
    @Column(name = "response_json")
    private String responseJson;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "server_name", length = 10)
    private String serverName;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}

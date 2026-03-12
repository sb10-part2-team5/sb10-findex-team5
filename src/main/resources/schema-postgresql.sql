CREATE TABLE index_info (
                            id                   uuid DEFAULT gen_random_uuid() PRIMARY KEY,
                            index_name           varchar(100) NOT NULL,
                            index_classification varchar(50)  NOT NULL,
                            constituent_count    int          NOT NULL,
                            base_date            date         NOT NULL,
                            base_value           decimal(19,4), -- 대략 1경까지 표현 가능
                            source_type          varchar(30)  NOT NULL,
                            is_favorite          boolean      NOT NULL DEFAULT false,
                            created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,

                            CONSTRAINT uq_index_info_name_classification
                                UNIQUE (index_name, index_classification),

                            CONSTRAINT ck_index_info_source_type
                                CHECK (source_type IN ('USER', 'OPEN_API')) -- ENUM 사용 대비
);

CREATE TABLE index_data (
                            id            uuid DEFAULT gen_random_uuid() PRIMARY KEY,
                            index_id      uuid           NOT NULL,
                            trade_date    date           NOT NULL,
                            source_type   varchar(30)    NOT NULL,
                            open_value    decimal(19,4) NOT NULL,
                            close_value   decimal(19,4) NOT NULL,
                            high_value    decimal(19,4) NOT NULL,
                            low_value     decimal(19,4) NOT NULL,
                            change_amount decimal(19,4) NOT NULL,
                            change_rate   decimal(9,4)  NOT NULL, --99999.9999% 까지 등락률 표현 가능
                            volume        bigint        NOT NULL,
                            trade_amount  decimal(19,4) NOT NULL,
                            market_cap    decimal(19,4) NOT NULL,
                            created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,

                            CONSTRAINT uq_index_data_index_id_trade_date
                                UNIQUE (index_id, trade_date),

                            CONSTRAINT fk_index_data_index_info
                                FOREIGN KEY (index_id)
                                    REFERENCES index_info(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT ck_index_data_source_type
                                CHECK (source_type IN ('USER', 'OPEN_API')) -- ENUM 사용 대비
);

CREATE TABLE integration_task (
                                  id            uuid DEFAULT gen_random_uuid() PRIMARY KEY,
                                  index_id      uuid        NOT NULL,
                                  task_type     varchar(30) NOT NULL,
                                  target_date   date,
                                  operator      varchar(45) NOT NULL,
                                  task_at       TIMESTAMP WITH TIME ZONE NOT NULL,
                                  result        varchar(20) NOT NULL,
                                  error_message text,
                                  created_at    TIMESTAMP WITH TIME ZONE NOT NULL,

                                  CONSTRAINT fk_integration_task_index_info
                                      FOREIGN KEY (index_id)
                                          REFERENCES index_info(id),

                                  CONSTRAINT ck_integration_task_type
                                      CHECK (task_type IN ('INDEX_INFO', 'INDEX_DATA')), -- ENUM 사용 대비

                                  CONSTRAINT ck_integration_task_result
                                      CHECK (result IN ('SUCCESS', 'FAILURE')) -- ENUM 사용 대비
);

CREATE TABLE auto_integration_setting (
                                          id         uuid DEFAULT gen_random_uuid() PRIMARY KEY,
                                          index_id   uuid        NOT NULL,
                                          enabled    boolean     NOT NULL DEFAULT false,
                                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                          CONSTRAINT uq_auto_integration_setting_index_id
                                              UNIQUE (index_id),

                                          CONSTRAINT fk_auto_integration_setting_index_info
                                              FOREIGN KEY (index_id)
                                                  REFERENCES index_info(id)
                                                  ON DELETE CASCADE
);

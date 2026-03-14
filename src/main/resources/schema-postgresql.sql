CREATE TABLE index_info
(
    id                   uuid                              DEFAULT gen_random_uuid() PRIMARY KEY,
    index_name           varchar(100)             NOT NULL,
    index_classification varchar(50)              NOT NULL,
    employed_items_count int                      NOT NULL,
    base_point_in_time   date                     NOT NULL,
    base_index           decimal(19, 4)           NOT NULL,  -- 대략 1경까지 표현 가능
    source_type          varchar(30)              NOT NULL,
    favorite             boolean                  NOT NULL DEFAULT false,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_index_info_name_classification
        UNIQUE (index_name, index_classification),

    CONSTRAINT ck_index_info_source_type
        CHECK (source_type IN ('USER', 'OPEN_API')) -- ENUM 사용 대비
);

CREATE TABLE index_data
(
    id            uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    index_info_id uuid                     NOT NULL,
    base_date     date                     NOT NULL,
    source_type   varchar(30)              NOT NULL,
    market_price  decimal(19, 4)           NOT NULL,
    closing_price decimal(19, 4)           NOT NULL,
    high_price    decimal(19, 4)           NOT NULL,
    low_price     decimal(19, 4)           NOT NULL,
    versus        decimal(19, 4)           NOT NULL,
    fluctuation_rate decimal(9, 4)         NOT NULL, --99999.9999% 까지 등락률 표현 가능
    trading_quantity bigint                NOT NULL,
    trading_price bigint                   NOT NULL,
    market_total_amount bigint             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_index_data_index_info_id_base_date
        UNIQUE (index_info_id, base_date),

    CONSTRAINT fk_index_data_index_info
        FOREIGN KEY (index_info_id)
            REFERENCES index_info (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_index_data_source_type
        CHECK (source_type IN ('USER', 'OPEN_API'))  -- ENUM 사용 대비
);

CREATE TABLE integration_task
(
    id            uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    index_info_id uuid                     NOT NULL,
    job_type      varchar(30)              NOT NULL,
    target_date   date,
    worker        varchar(45)              NOT NULL,
    job_time      TIMESTAMP WITH TIME ZONE NOT NULL,
    result        varchar(20)              NOT NULL,
    error_message text,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_integration_task_index_info
        FOREIGN KEY (index_info_id)
            REFERENCES index_info (id),

    CONSTRAINT ck_integration_task_type
        CHECK (job_type IN ('INDEX_INFO', 'INDEX_DATA')), -- ENUM 사용 대비

    CONSTRAINT ck_integration_task_result
        CHECK (result IN ('SUCCESS', 'FAILED'))           -- ENUM 사용 대비
);

CREATE TABLE auto_sync_config
(
    id            uuid                              DEFAULT gen_random_uuid() PRIMARY KEY,
    index_info_id uuid                     NOT NULL,
    enabled       boolean                  NOT NULL DEFAULT false,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uq_auto_sync_config_index_info_id
        UNIQUE (index_info_id),

    CONSTRAINT fk_auto_sync_config_index_info
        FOREIGN KEY (index_info_id)
            REFERENCES index_info (id)
            ON DELETE CASCADE
);

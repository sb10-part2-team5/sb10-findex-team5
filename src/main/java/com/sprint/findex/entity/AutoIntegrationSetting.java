package com.sprint.findex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "auto_integration_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoIntegrationSetting extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = false;
}
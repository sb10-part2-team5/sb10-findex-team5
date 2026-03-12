package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "auto_integration_setting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoIntegrationSetting extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;
}

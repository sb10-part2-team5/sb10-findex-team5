package com.sprint.findex.entity;

import com.sprint.findex.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "auto_integration")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AutoIntegration extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_info_id", nullable = false, unique = true)
    private IndexInfo indexInfo;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public static AutoIntegration create(
            IndexInfo indexInfo
    ) {
        return new AutoIntegration(
                indexInfo,
                false
        );
    }
}

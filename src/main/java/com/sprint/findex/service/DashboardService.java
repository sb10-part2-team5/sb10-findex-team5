package com.sprint.findex.service;

import com.sprint.findex.dto.dashboard.DashboardIndexResponse;
import com.sprint.findex.repository.DashboardQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardQueryRepository dashboardQueryRepository;

    public List<DashboardIndexResponse> getFavoriteIndexSummaries()
}

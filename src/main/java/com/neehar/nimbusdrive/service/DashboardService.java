package com.neehar.nimbusdrive.service;

import com.neehar.nimbusdrive.dto.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(String email);
}
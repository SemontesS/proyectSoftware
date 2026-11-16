package application.adapters.rest.dtos.responses;

import application.adapters.rest.dtos.shared.*;

import java.util.Map;

public record AdministrativeReportResponseDTO(Map<String, Long> usersByRole,
        Map<String, Long> productsByStatus, Map<String, Long> ordersByStatus,
        Map<String, MoneyDTO> salesTotals) {}

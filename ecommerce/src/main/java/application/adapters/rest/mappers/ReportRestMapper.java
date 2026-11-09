package application.adapters.rest.mappers;

import application.adapters.rest.dtos.responses.AdministrativeReportResponseDTO;
import application.adapters.rest.dtos.responses.AuditLogResponseDTO;
import application.adapters.rest.dtos.shared.MoneyDTO;
import application.domain.models.AdministrativeReport;
import application.domain.models.AuditLog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ReportRestMapper {

    private ReportRestMapper() {
    }

    public static AuditLogResponseDTO toAuditDTO(AuditLog log) {
        return new AuditLogResponseDTO(log.auditId(), log.userId(), log.action().name(), log.entityType(),
                log.entityId(), log.detail(), log.timestamp());
    }

    public static List<AuditLogResponseDTO> toAuditDTOs(List<AuditLog> logs) {
        return logs.stream().map(ReportRestMapper::toAuditDTO).toList();
    }

    public static AdministrativeReportResponseDTO toReportDTO(AdministrativeReport report) {
        return new AdministrativeReportResponseDTO(
                byName(report.usersByRole(), Enum::name, count -> count),
                byName(report.productsByStatus(), Enum::name, count -> count),
                byName(report.ordersByStatus(), Enum::name, count -> count),
                byName(report.salesTotals(), Enum::name, CommonRestMapper::toDTO));
    }

    private static <K, V, R> Map<String, R> byName(Map<K, V> source, Function<K, String> key, Function<V, R> value) {
        Map<String, R> result = new LinkedHashMap<>();
        source.forEach((k, v) -> result.put(key.apply(k), value.apply(v)));
        return result;
    }
}

package application.domain.services.reporting;

import application.domain.models.AdministrativeReport;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.authorization.ValidateUserPermissionsService;
import application.domain.valueobjects.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Consolidated administrative information for administrators and supervisors. */
@Service
@RequiredArgsConstructor
public class ConsultAdministrativeReportsService {
    private final UserRepositoryPort userRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ValidateUserPermissionsService permissions;

    public AdministrativeReport consult(User requestingUser) {
        permissions.validate(requestingUser, Permission.REPORT_CONSULT);
        return new AdministrativeReport(
                userRepositoryPort.countByRole(),
                productRepositoryPort.countByStatus(),
                orderRepositoryPort.countByStatus(),
                orderRepositoryPort.salesTotalsByCurrency());
    }
}

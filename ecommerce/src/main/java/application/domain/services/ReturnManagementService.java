package application.domain.services;

import application.domain.models.Return;
import application.domain.models.Order;
import application.domain.valueobjects.ReturnStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * ReturnManagementService coordina el proceso de return relacionado
 * with un order.
 *
 * In esta versión del dominio no se inventan condiciones additional de
 * return (plazo máximo, categories excluidas, porcentajes, etc.)
 * porque la especificación funcional no las define todavía. El servicio
 * se limita a las reglas que sí están confirmadas.
 *
 * Fuente: Domain Services — NexusMarket, sección 7.
 */
public class ReturnManagementService {

    /**
     * Records la solicitud de return associated a un order.
     *
     * @param returnId   identificador a asignar a la return
     * @param order         order sobre el cual se solicita la return
     * @param reason         reason de la return
     * @param date          date de la solicitud
     * @param initialStatus  status inicial de la return
     * @return la Return creada, associated al order
     */
    public Return requestReturn(Long returnId,
                                           Order order,
                                           String reason,
                                           LocalDate date,
                                           ReturnStatus initialStatus) {

        Objects.requireNonNull(order, "order es obligatorio");

        // Rule confirmada por la especificación: un order finalizado no
        // puede modificarse. Solicitar una return no modifica el
        // order en sí (no changes sus líneas ni su total), por lo que esta
        // regla no impide crear la return; el order conserva su
        // status. Cualquier condición additional (p. ej. exigir que el
        // order esté DELIVERED) queda pendiente de definición funcional
        // y no se inventa aquí.

        // 3-4. Crear la return y asociarla al order.
        return new Return(returnId, order.getOrderId(), reason, date, initialStatus);

        // 5. El proceso posterior (RefundGenerationService) determina
        //    si corresponde un refund.
    }
}

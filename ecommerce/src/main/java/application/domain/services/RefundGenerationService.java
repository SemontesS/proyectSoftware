package application.domain.services;

import application.domain.models.Return;
import application.domain.models.Refund;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.RefundStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * RefundGenerationService coordina la generación de un refund a
 * partir de una return.
 *
 * Rule del dominio: una return puede generar como máximo un
 * refund. Esa invariante la protege el propio added Return
 * (ver Return#generateRefund); este servicio coordina la operación
 * y determina el amount a partir de la información disponible.
 *
 * Fuente: Domain Services — NexusMarket, sección 8.
 */
public class RefundGenerationService {

    /**
     * Generates el refund associated a una return.
     *
     * @param refundId    identificador a asignar al refund
     * @param return     return que origina el refund
     * @param amount          amount determinado para el refund
     * @param date          date del refund
     * @param initialStatus  status inicial del refund
     * @return el Refund creado y associated a la return
     */
    public Refund generateRefund(Long refundId,
                                       Return returnRequest,
                                       Money amount,
                                       LocalDate date,
                                       RefundStatus initialStatus) {

        Objects.requireNonNull(returnRequest, "return es obligatoria");
        Objects.requireNonNull(amount, "amount es obligatorio");

        // 2-4. Validate que la return pueda generar un refund, y
        //      crearlo. Return.generateRefund ya impide que una
        //      misma return genere más de un refund (0..1).
        return returnRequest.generateRefund(refundId, amount, date, initialStatus);
    }
}

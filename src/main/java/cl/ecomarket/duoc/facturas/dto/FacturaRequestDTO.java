package cl.ecomarket.duoc.facturas.dto;

import lombok.Data;

@Data
public class FacturaRequestDTO {
    private Long ventaId;
    private Long clienteId;
}

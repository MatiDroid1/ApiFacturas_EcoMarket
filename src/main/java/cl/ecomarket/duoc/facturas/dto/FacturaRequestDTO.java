package cl.ecomarket.duoc.facturas.dto;

import lombok.Data;

@Data
public class FacturaRequestDTO {
    public Long facturaId;
    private Long ventaId;
    private Long clienteId;
}

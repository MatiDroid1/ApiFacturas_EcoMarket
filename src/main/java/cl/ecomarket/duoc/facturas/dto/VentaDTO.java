package cl.ecomarket.duoc.facturas.dto;

import lombok.Data;

@Data
public class VentaDTO {
    private Long ventaId;
    private Long pedidoId;
    private Double total;
    private String metodoPago;
    private String estado;
}
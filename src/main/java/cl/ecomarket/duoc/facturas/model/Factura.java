package cl.ecomarket.duoc.facturas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facturaId;

    private Long ventaId;
    private Long clienteId;

    private LocalDate fechaEmision;
    private Double subtotal;
    private Double iva;
    private Double total;

    private String metodoPago;
    private String nombreCliente;
    private String emailCliente;
}
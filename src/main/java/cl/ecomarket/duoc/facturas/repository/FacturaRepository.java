package cl.ecomarket.duoc.facturas.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.ecomarket.duoc.facturas.model.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByClienteId(Long clienteId);
}
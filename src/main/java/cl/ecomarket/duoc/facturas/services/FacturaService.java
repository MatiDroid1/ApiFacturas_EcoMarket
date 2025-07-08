package cl.ecomarket.duoc.facturas.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cl.ecomarket.duoc.facturas.dto.FacturaRequestDTO;
import cl.ecomarket.duoc.facturas.dto.UsuarioDTO;
import cl.ecomarket.duoc.facturas.dto.VentaDTO;
import cl.ecomarket.duoc.facturas.model.Factura;
import cl.ecomarket.duoc.facturas.repository.FacturaRepository;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository repo;
    @Autowired
    private RestTemplate restTemplate;

    private final String urlVentas = "http://localhost:8090/api/v1/ventas/";
    private final String urlUsuarios = "http://localhost:8080/api/v1/usuarios/id/";

    public List<Factura> listar() {
        return repo.findAll();
    }

    public Factura obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

public Factura emitirFactura(FacturaRequestDTO req) {
    if (req.getVentaId() == null || req.getClienteId() == null) {
        throw new IllegalArgumentException("ventaId y clienteId no pueden ser null");
    }

    VentaDTO venta = restTemplate.getForObject(urlVentas + req.getVentaId(), VentaDTO.class);
    UsuarioDTO usuario = restTemplate.getForObject(urlUsuarios + req.getClienteId(), UsuarioDTO.class);

    if (venta == null || usuario == null) {
        throw new RuntimeException("No se pudo obtener la información de venta o cliente");
    }

    double subtotal = venta.getTotal();
    double iva = subtotal * 0.19;
    double total = subtotal + iva;

    Factura factura = new Factura();
    factura.setVentaId(req.getVentaId());
    factura.setClienteId(req.getClienteId());
    factura.setFechaEmision(LocalDate.now());
    factura.setSubtotal(subtotal);
    factura.setIva(iva);
    factura.setTotal(total);
    factura.setMetodoPago(venta.getMetodoPago());
    factura.setNombreCliente(usuario.getNombre());
    factura.setEmailCliente(usuario.getEmail());

    return repo.save(factura);
}


}
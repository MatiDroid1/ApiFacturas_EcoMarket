package cl.ecomarket.duoc.facturas.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import cl.ecomarket.duoc.facturas.controller.FacturaController;
import cl.ecomarket.duoc.facturas.dto.FacturaRequestDTO;
import cl.ecomarket.duoc.facturas.model.Factura;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



@Component
public class FacturaModelAssembler implements RepresentationModelAssembler<Factura, EntityModel<Factura>> {

    @Override
    public EntityModel<Factura> toModel(Factura factura) {
        return EntityModel.of(factura,
                linkTo(methodOn(FacturaController.class).obtenerPorId(factura.getFacturaId())).withSelfRel(),
                linkTo(methodOn(FacturaController.class).listar()).withRel("facturas"),
                // Link al usuario (otro microservicio)
                // Esto es un link plano porque es externo
                org.springframework.hateoas.Link.of("http://localhost:8080/api/v1/usuarios/id/" + factura.getClienteId()).withRel("cliente"),
                // Link a la venta (otro microservicio)
                org.springframework.hateoas.Link.of("http://localhost:8090/api/v1/ventas/" + factura.getVentaId()).withRel("venta")
        );
    }
}
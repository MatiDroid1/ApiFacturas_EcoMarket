package cl.ecomarket.duoc.facturas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ecomarket.duoc.facturas.assembler.FacturaModelAssembler;
import cl.ecomarket.duoc.facturas.dto.FacturaRequestDTO;
import cl.ecomarket.duoc.facturas.model.Factura;
import cl.ecomarket.duoc.facturas.services.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/facturas")
@CrossOrigin
@Tag(name = "Facturas", description = "Gestion de facturas")
public class FacturaController {

    @Autowired
    private FacturaService service;

    @Autowired
    private FacturaModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todas las facturas")
    public ResponseEntity<CollectionModel<EntityModel<Factura>>> listar() {
        List<Factura> facturas = service.listar();

        List<EntityModel<Factura>> modelos = facturas.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Factura>> body = CollectionModel.of(
                modelos,
                linkTo(methodOn(FacturaController.class).listar()).withSelfRel()
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura por ID")
    public ResponseEntity<EntityModel<Factura>> obtenerPorId(@PathVariable Long id) {
        Factura factura = service.obtenerPorId(id);
        if (factura == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(assembler.toModel(factura));
    }

    @PostMapping
    @Operation(summary = "Emitir una nueva factura")
    public ResponseEntity<EntityModel<Factura>> crear(@RequestBody FacturaRequestDTO request) {
        try {
            Factura facturaCreada = service.emitirFactura(request);

            EntityModel<Factura> modelo = assembler.toModel(facturaCreada);

            URI location = linkTo(methodOn(FacturaController.class)
                            .obtenerPorId(facturaCreada.getFacturaId()))
                            .toUri();

            return ResponseEntity
                    .created(location)
                    .body(modelo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
package cl.ecomarket.duoc.facturas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ecomarket.duoc.facturas.dto.FacturaRequestDTO;
import cl.ecomarket.duoc.facturas.model.Factura;
import cl.ecomarket.duoc.facturas.services.FacturaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/facturas")
@CrossOrigin
@Tag(name="Facturas", description = "Gestion de facturas")
public class FacturaController {

    @Autowired
    private FacturaService service;

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    

@PostMapping
@Operation(summary = "Emitir una nueva factura")
public ResponseEntity<?> crear(@RequestBody FacturaRequestDTO request) {
    try {
        Factura facturaCreada = service.emitirFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaCreada);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}

}
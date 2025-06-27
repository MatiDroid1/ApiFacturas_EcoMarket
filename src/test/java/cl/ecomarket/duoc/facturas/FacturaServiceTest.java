package cl.ecomarket.duoc.facturas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import cl.ecomarket.duoc.facturas.dto.FacturaRequestDTO;
import cl.ecomarket.duoc.facturas.dto.UsuarioDTO;
import cl.ecomarket.duoc.facturas.dto.VentaDTO;
import cl.ecomarket.duoc.facturas.model.Factura;
import cl.ecomarket.duoc.facturas.repository.FacturaRepository;
import cl.ecomarket.duoc.facturas.services.FacturaService;

public class FacturaServiceTest {

    @InjectMocks
    private FacturaService facturaService;

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void emitirFactura_CuandoDatosValidos_DevuelveFacturaCreada() {
        // Preparar datos de entrada
        FacturaRequestDTO req = new FacturaRequestDTO();
        req.setVentaId(1L);
        req.setClienteId(100L);

        // Mockear respuesta de venta
        VentaDTO ventaMock = new VentaDTO();
        ventaMock.setTotal(1000.0);
        ventaMock.setMetodoPago("transferencia");

        // Mockear respuesta de usuario
        UsuarioDTO usuarioMock = new UsuarioDTO();
        //usuarioMock.setNombre("Juan Perez");
        //usuarioMock.setEmail("juan@example.com");

        // Mockear RestTemplate para que devuelva los DTOs mockeados
        when(restTemplate.getForObject("http://localhost:8090/api/v1/ventas/5", VentaDTO.class))
                .thenReturn(ventaMock);
        when(restTemplate.getForObject("http://localhost:8080/api/v1/usuarios/id/26", UsuarioDTO.class))
                .thenReturn(usuarioMock);

        // Mockear repo.save para devolver la misma factura
        ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
        when(facturaRepository.save(facturaCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar método a probar
        Factura facturaCreada = facturaService.emitirFactura(req);

        // Validar resultados
        assertNotNull(facturaCreada);
        assertEquals(req.getVentaId(), facturaCreada.getVentaId());
        assertEquals(req.getClienteId(), facturaCreada.getClienteId());
        assertEquals(1000.0, facturaCreada.getSubtotal());
        assertEquals(1000.0 * 0.19, facturaCreada.getIva());
        assertEquals(1000.0 * 1.19, facturaCreada.getTotal());
        assertEquals("transferencia", facturaCreada.getMetodoPago());
        assertEquals("Juan Perez", facturaCreada.getNombreCliente());
        assertEquals("juan@example.com", facturaCreada.getEmailCliente());
        assertEquals(LocalDate.now(), facturaCreada.getFechaEmision());

        // Además, verificar que repo.save fue llamado una vez
        verify(facturaRepository, times(1)).save(any(Factura.class));
    }

    @Test
    public void emitirFactura_CuandoVentaIdEsNull_LanzaIllegalArgumentException() {
        FacturaRequestDTO req = new FacturaRequestDTO();
        req.setVentaId(null);
        req.setClienteId(100L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            facturaService.emitirFactura(req);
        });

        assertEquals("ventaId y clienteId no pueden ser null", exception.getMessage());
    }

    @Test
    public void emitirFactura_CuandoUsuarioOventaSonNull_LanzaRuntimeException() {
        FacturaRequestDTO req = new FacturaRequestDTO();
        req.setVentaId(1L);
        req.setClienteId(100L);

        when(restTemplate.getForObject("http://localhost:8090/api/v1/ventas/5", VentaDTO.class))
                .thenReturn(null);
        when(restTemplate.getForObject("http://localhost:8080/api/v1/usuarios/id/26", UsuarioDTO.class))
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            facturaService.emitirFactura(req);
        });

        assertEquals("No se pudo obtener la información de venta o cliente", exception.getMessage());
    }
}
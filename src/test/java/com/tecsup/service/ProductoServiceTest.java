package com.tecsup.service;

import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository repo;

    @InjectMocks
    private ProductoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // TEST LISTAR
    @Test
    void testListarProductos() {
        List<Producto> lista = List.of(
                new Producto(1L, "Monitor", 500, 10, "Tecnologia")
        );
        when(repo.findAll()).thenReturn(lista);
        List<Producto> resultado = service.listar();
        assertEquals(1, resultado.size());
        assertEquals("Monitor", resultado.get(0).getNombre());
        verify(repo, times(1)).findAll();
    }

    // TEST GUARDAR
    @Test
    void testGuardarProducto() {
        Producto producto = new Producto(1L, "Laptop", 3000, 5, "Computo");
        when(repo.save(producto)).thenReturn(producto);
        Producto resultado = service.guardar(producto);
        assertNotNull(resultado);
        assertEquals("Laptop", resultado.getNombre());
        verify(repo).save(producto);
    }

    // TEST OBTENER
    @Test
    void testObtenerProducto() {
        Producto producto = new Producto(1L, "Teclado", 100, 20, "Perifericos");
        when(repo.findById(1L)).thenReturn(Optional.of(producto));
        Producto resultado = service.obtener(1L);
        assertNotNull(resultado);
        assertEquals("Teclado", resultado.getNombre());
        verify(repo).findById(1L);
    }

    // TEST OBTENER cuando el producto no existe
    @Test
    void testObtenerProductoNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        Producto resultado = service.obtener(99L);
        assertNull(resultado);
        verify(repo).findById(99L);
    }

    // TEST ACTUALIZAR
    @Test
    void testActualizarProducto() {
        Producto existente = new Producto(1L, "Mouse", 80, 20, "Perifericos");
        Producto datos = new Producto(null, "Mouse Gamer", 120, 15, "Gaming");
        Producto actualizado = new Producto(1L, "Mouse Gamer", 120, 15, "Gaming");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Producto.class))).thenReturn(actualizado);
        Producto resultado = service.actualizar(1L, datos);
        assertNotNull(resultado);
        assertEquals("Mouse Gamer", resultado.getNombre());
        assertEquals(120, resultado.getPrecio());
        assertEquals("Gaming", resultado.getCategoria());
        verify(repo).findById(1L);
        verify(repo).save(existente);
    }

    // TEST ACTUALIZAR cuando el producto no existe
    @Test
    void testActualizarProductoNoExiste() {
        Producto datos = new Producto(null, "Inexistente", 10, 1, "Test");
        when(repo.findById(99L)).thenReturn(Optional.empty());
        Producto resultado = service.actualizar(99L, datos);
        assertNull(resultado);
        verify(repo).findById(99L);
        verify(repo, never()).save(any());
    }

    // TEST ELIMINAR
    @Test
    void testEliminarProducto() {
        doNothing().when(repo).deleteById(1L);
        service.eliminar(1L);
        verify(repo, times(1)).deleteById(1L);
    }
}

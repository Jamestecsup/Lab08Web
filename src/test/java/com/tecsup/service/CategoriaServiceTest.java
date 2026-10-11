package com.tecsup.service;

import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repo;

    @InjectMocks
    private CategoriaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarCategorias() {
        List<Categoria> lista = List.of(new Categoria(1L, "Tecnologia", "Productos tecnologicos"));
        when(repo.findAll()).thenReturn(lista);
        List<Categoria> resultado = service.listar();
        assertEquals(1, resultado.size());
        assertEquals("Tecnologia", resultado.get(0).getNombre());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testGuardarCategoria() {
        Categoria categoria = new Categoria(1L, "Hogar", "Articulos del hogar");
        when(repo.save(categoria)).thenReturn(categoria);
        Categoria resultado = service.guardar(categoria);
        assertNotNull(resultado);
        assertEquals("Hogar", resultado.getNombre());
        verify(repo).save(categoria);
    }

    @Test
    void testObtenerCategoria() {
        Categoria categoria = new Categoria(1L, "Deportes", "Articulos deportivos");
        when(repo.findById(1L)).thenReturn(Optional.of(categoria));
        Categoria resultado = service.obtener(1L);
        assertNotNull(resultado);
        assertEquals("Deportes", resultado.getNombre());
        verify(repo).findById(1L);
    }

    @Test
    void testObtenerCategoriaNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertNull(service.obtener(99L));
        verify(repo).findById(99L);
    }

    @Test
    void testActualizarCategoria() {
        Categoria existente = new Categoria(1L, "Ropa", "Prendas");
        Categoria datos = new Categoria(null, "Ropa Deportiva", "Prendas deportivas");
        Categoria actualizada = new Categoria(1L, "Ropa Deportiva", "Prendas deportivas");
        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(any(Categoria.class))).thenReturn(actualizada);
        Categoria resultado = service.actualizar(1L, datos);
        assertNotNull(resultado);
        assertEquals("Ropa Deportiva", resultado.getNombre());
        verify(repo).findById(1L);
        verify(repo).save(existente);
    }

    @Test
    void testActualizarCategoriaNoExiste() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertNull(service.actualizar(99L, new Categoria(null, "X", "Y")));
        verify(repo, never()).save(any());
    }

    @Test
    void testEliminarCategoria() {
        doNothing().when(repo).deleteById(1L);
        service.eliminar(1L);
        verify(repo, times(1)).deleteById(1L);
    }
}

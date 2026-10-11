package com.tecsup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.ProductoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoDTO nuevoProducto(String nombre, double precio, int stock, String categoria) {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setCategoria(categoria);
        return dto;
    }

    // TEST POST
    @Test
    void testGuardarProducto() throws Exception {
        ProductoDTO dto = nuevoProducto("Mouse", 80, 20, "Perifericos");
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    // TEST GET
    @Test
    void testListarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    // TEST VALIDACION
    @Test
    void testValidacionNombreVacio() throws Exception {
        ProductoDTO dto = nuevoProducto("", 10, 2, "Test");
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // TEST PUT /api/productos/{id}
    @Test
    void testActualizarProducto() throws Exception {
        ProductoDTO creado = nuevoProducto("Teclado", 100, 10, "Perifericos");
        MvcResult result = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creado)))
                .andExpect(status().isCreated())
                .andReturn();
        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        ProductoDTO actualizado = nuevoProducto("Teclado Mecanico", 150, 8, "Gaming");
        mockMvc.perform(put("/api/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado Mecanico"))
                .andExpect(jsonPath("$.precio").value(150.0));
    }

    // TEST PUT con id inexistente -> 404
    @Test
    void testActualizarProductoNoExiste() throws Exception {
        ProductoDTO dto = nuevoProducto("Inexistente", 10, 1, "Test");
        mockMvc.perform(put("/api/productos/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // TEST DELETE /api/productos/{id}
    @Test
    void testEliminarProducto() throws Exception {
        ProductoDTO dto = nuevoProducto("ParaEliminar", 50, 5, "Test");
        MvcResult result = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/productos/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isNotFound());
    }

    // TEST DELETE con id inexistente -> 404
    @Test
    void testEliminarProductoNoExiste() throws Exception {
        mockMvc.perform(delete("/api/productos/{id}", 99999))
                .andExpect(status().isNotFound());
    }
}

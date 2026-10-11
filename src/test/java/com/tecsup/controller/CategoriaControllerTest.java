package com.tecsup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.CategoriaDTO;
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
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoriaDTO nuevaCategoria(String nombre, String descripcion) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre(nombre);
        dto.setDescripcion(descripcion);
        return dto;
    }

    @Test
    void testGuardarCategoria() throws Exception {
        CategoriaDTO dto = nuevaCategoria("Tecnologia", "Productos tecnologicos");
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Tecnologia"));
    }

    @Test
    void testListarCategorias() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidacionNombreVacio() throws Exception {
        CategoriaDTO dto = nuevaCategoria("", "Sin nombre");
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActualizarCategoria() throws Exception {
        CategoriaDTO dto = nuevaCategoria("Hogar", "Articulos del hogar");
        MvcResult result = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        CategoriaDTO actualizado = nuevaCategoria("Hogar Premium", "Articulos premium");
        mockMvc.perform(put("/api/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hogar Premium"));
    }

    @Test
    void testActualizarCategoriaNoExiste() throws Exception {
        CategoriaDTO dto = nuevaCategoria("Inexistente", "No existe");
        mockMvc.perform(put("/api/categorias/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarCategoria() throws Exception {
        CategoriaDTO dto = nuevaCategoria("Temporal", "Para eliminar");
        MvcResult result = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/categorias/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/categorias/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarCategoriaNoExiste() throws Exception {
        mockMvc.perform(delete("/api/categorias/{id}", 99999))
                .andExpect(status().isNotFound());
    }
}

package com.example.Api;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerAddUpdateTests {

    @Autowired
    private MockMvc mockMvc;

    
     
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreatePost() throws Exception {
        System.out.println("Crear Un Nuevo Cliente");
        String nombres = "John Doe";
        int cedula = 1234567892;
        String direccion = "123 Main St";
        int phoneNumber = 1234567846;

        mockMvc.perform(post("/api/posts")
                .param("nombres", nombres)
                .param("cedula", String.valueOf(cedula))
                .param("direccion", direccion)
                .param("phoneNumber", String.valueOf(phoneNumber)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Cliente creado exitosamente"))
                .andExpect(jsonPath("$.example.nombres").value(nombres))
                .andExpect(jsonPath("$.example.cedula").value(String.valueOf(cedula)))
                .andExpect(jsonPath("$.example.direccion").value(direccion))
                .andExpect(jsonPath("$.example.phoneNumber").value(String.valueOf(phoneNumber)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdatePost() throws Exception {
        System.out.println("Actualizar Cliente");
        int postId = 1; // Reemplaza con el ID del post que deseas actualizar
        String nombres = "John Doe Doe";
        int cedula = 1234567890;
        String direccion = "123 Main St sr";
        int phoneNumber = 1234567890;

        mockMvc.perform(put("/api/posts/{postId}", postId)
                .param("nombres", nombres)
                .param("cedula", String.valueOf(cedula))
                .param("direccion", direccion)
                .param("phoneNumber", String.valueOf(phoneNumber)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("El cliente con la id proporcionada ya existe. Se realizará la actualización."))
                .andExpect(jsonPath("$.example.id").value(postId))
                .andExpect(jsonPath("$.example.nombres").value(nombres))
                .andExpect(jsonPath("$.example.cedula").value(String.valueOf(cedula)))
                .andExpect(jsonPath("$.example.direccion").value(direccion))
                .andExpect(jsonPath("$.example.phoneNumber").value(String.valueOf(phoneNumber)));
    }
    
}

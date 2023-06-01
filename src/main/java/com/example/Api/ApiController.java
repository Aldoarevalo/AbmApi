package com.example.Api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

@RestController
@RequestMapping("/api")
public class ApiController {
    private Connection connection;

    public ApiController() {
        try {
            // Establecer la conexión con la base de datos SQLite
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try {
            Statement statement = connection.createStatement();
            // Verificar si la tabla "Clientes" existe, de lo contrario crearla
            ResultSet resultSet = statement.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='clientes'"
            );
            if (!resultSet.next()) {
                statement.execute(
                        "CREATE TABLE clientes (id INTEGER PRIMARY KEY AUTOINCREMENT, nombres TEXT, cedula TEXT, direccion TEXT, phoneNumber INTEGER)"
                );

                // Insertar clientes por defecto
                statement.execute(
                        "INSERT INTO clientes (nombres, cedula, direccion, phoneNumber) " +
                        "VALUES ('Cliente 1', '123456789', 'Dirección 1', 1234567890)"
                );
                statement.execute(
                        "INSERT INTO clientes (nombres, cedula, direccion, phoneNumber) " +
                        "VALUES ('Cliente 2', '987654321', 'Dirección 2', 9876543210)"
                );
                // Puedes agregar más inserciones de clientes según tus necesidades
                
                System.out.println("Tabla 'clientes' creada y clientes por defecto insertados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/posts")
    @ApiOperation("Obtener todos los Clientes")
    public List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM clientes");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombres = resultSet.getString("nombres");
                String cedula = resultSet.getString("cedula");
                String direccion = resultSet.getString("direccion");
                String phoneNumber = resultSet.getString("phoneNumber");
                posts.add(new Post(id, nombres, cedula, direccion, phoneNumber));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @PostMapping("/posts")
    @ApiOperation("Crear un nuevo Cliente")
    public ResponseEntity<?> createPost(
            @ApiParam(value = "Nombres y Apellidos del Cliente", required = true) @RequestParam("nombres") String nombres,
            @ApiParam(value = "Cédula", required = true) @RequestParam("cedula")  @Digits(integer = 10, fraction = 0) int cedula,
            @ApiParam(value = "Dirección", required = true) @RequestParam("direccion") String direccion,
            @ApiParam(value = "Número de teléfono", required = true) @RequestParam("phoneNumber") @Digits(integer = 10, fraction = 0) int phoneNumber
    ) {
        try {
            // Verificar si el Cliente ya existe en la base de datos
            String checkSql = "SELECT * FROM clientes WHERE cedula = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, cedula);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // El Cliente ya existe en la base de datos
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "El Cliente con el número de cédula proporcionado ya existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson.toString());
            } else {
                // Insertar el nuevo Cliente en la base de datos
                String insertSql = "INSERT INTO clientes (nombres, cedula, direccion, phoneNumber) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                insertStatement.setString(1, nombres);
                insertStatement.setInt(2, cedula);
                insertStatement.setString(3, direccion);
                insertStatement.setInt(4, phoneNumber);
                insertStatement.executeUpdate();

                // Construir el objeto JSON con el mensaje de éxito y el valor de ejemplo
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "Cliente creado exitosamente");
                JSONObject exampleValue = new JSONObject();
                exampleValue.put("nombres", nombres);
                exampleValue.put("cedula", cedula);
                exampleValue.put("direccion", direccion);
                exampleValue.put("phoneNumber", phoneNumber);
                responseJson.put("example", exampleValue);

                // Devolver una respuesta JSON con el mensaje de éxito y el valor de ejemplo
                return ResponseEntity.ok(responseJson.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // En caso de error en la base de datos, devolver una respuesta JSON con el mensaje de error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el Cliente");
    }


    @PutMapping("/posts/{postId}")
    @ApiOperation("Actualizar un Cliente existente")
    public ResponseEntity<?> updatePost(
            @ApiParam(value = "ID del Cliente a actualizar", required = true) @PathVariable("postId") int postId,
            @ApiParam(value = "Nombres y Apellidos del Cliente", required = true) @RequestParam("nombres") String nombres,
            @ApiParam(value = "Cédula", required = true) @RequestParam("cedula")  @Digits(integer = 10, fraction = 0) int cedula,
            @ApiParam(value = "Dirección", required = true) @RequestParam("direccion") String direccion,
            @ApiParam(value = "Número de teléfono", required = true) @RequestParam("phoneNumber") @Digits(integer = 10, fraction = 0) int phoneNumber

    ) {
        try {
            // Verificar si el Cliente existe en la base de datos
            String checkSql = "SELECT * FROM clientes WHERE id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, postId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // El post existe en la base de datos
                // Mostrar mensaje de advertencia
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "El cliente con la id proporcionada ya existe. Se realizará la actualización.");

                // Actualizar el post en la base de datos
                String sql = "UPDATE clientes SET nombres = ?, cedula = ?, direccion = ?, phoneNumber = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, nombres);
                preparedStatement.setInt(2, cedula);
                preparedStatement.setString(3, direccion);
                preparedStatement.setInt(4, phoneNumber);
                preparedStatement.setInt(5, postId);
                preparedStatement.executeUpdate();

                // Construir el objeto JSON con el mensaje de éxito y el valor de ejemplo
                JSONObject exampleValue = new JSONObject();
                exampleValue.put("id", postId);
                exampleValue.put("nombres", nombres);
                exampleValue.put("cedula", cedula);
                exampleValue.put("direccion", direccion);
                exampleValue.put("phoneNumber", phoneNumber);
                responseJson.put("example", exampleValue);

                // Devolver una respuesta JSON con el mensaje de éxito y el valor de ejemplo
                return ResponseEntity.ok(responseJson.toString());
            } else {
                // El post no existe en la base de datos
                JSONObject responseJson = new JSONObject();
                responseJson.put("message", "El Cliente con el id proporcionado no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // En caso de error en la base de datos, devolver una respuesta JSON con el mensaje de error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el Cliente");
    }




    @GetMapping("/posts/{id}")
    @ApiOperation("Obtener un post por Número de Cédula")
    public ResponseEntity<?> getPostById(@PathVariable("id") int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM clientes WHERE cedula = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
            	
            	String nombres = resultSet.getString("nombres");
            	String cedula = resultSet.getString("cedula");
            	String direccion = resultSet.getString("direccion");
            	String phoneNumber = resultSet.getString("phoneNumber");
            	Post post = new Post(id, nombres,cedula, direccion, phoneNumber);
            	return ResponseEntity.ok(post);
            	} else {
            	// Devolver una respuesta JSON con el mensaje de error si no se encontró el post
            	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el cliente con Ci " + id);
            	}
            	} catch (SQLException e) {
            	e.printStackTrace();
            	}
        // En caso de error en la base de datos, devolver una respuesta JSON con el mensaje de error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el Cliente");
    }
    
    @DeleteMapping("/posts/{id}")
    @ApiOperation("Eliminar un Cliente por id")
    public ResponseEntity<?> deletePostById(@PathVariable("id") int id) {
        try {
            // Verificar si el cliente existe en la base de datos
            String checkSql = "SELECT * FROM clientes WHERE id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, id);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Eliminar el post de la base de datos
                String deleteSql = "DELETE FROM clientes WHERE id = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                deleteStatement.setInt(1, id);
                deleteStatement.executeUpdate();
                
                // Devolver una respuesta JSON con el mensaje de éxito
                return ResponseEntity.ok("Cliente eliminado exitosamente");
            } else {
                // Devolver una respuesta JSON con el mensaje de error si no se encontró el post
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el cliente con id " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // En caso de error en la base de datos, devolver una respuesta JSON con el mensaje de error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el cliente");
    }



}
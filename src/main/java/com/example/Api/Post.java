package com.example.Api;


import javax.validation.constraints.Pattern;

public class Post {
    private int id;
    private String nombres;
    private String cedula;
    
   
    private String direccion;
    
    @Pattern(regexp = "\\d+", message = "El número de teléfono solo debe contener dígitos")
    private String phoneNumber;

    public Post() {
    }

    public Post(int id, String nombres, String cedula, String direccion, String phoneNumber) {
        this.id = id;
        this.nombres = nombres;
        this.cedula = cedula;
        this.direccion = direccion;
        this.phoneNumber = phoneNumber;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNobres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

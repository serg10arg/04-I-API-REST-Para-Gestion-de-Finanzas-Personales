package com.example.finanzaspersonales.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.HashSet;

/**
 * Entidad que representa a un usuario en el sistema de gestión de finanzas personales.
 * Un usuario tiene un identificador único, un nombre de usuario, una contraseña (hash)
 * y una lista de roles.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"categorias", "roles"})
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String contrasena; // Almacenar siempre la contraseña hash

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Categoria> categorias = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "usuarios_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.roles = new HashSet<>();
    }

    public void addRol(Rol rol) {
        this.roles.add(rol);
    }
}


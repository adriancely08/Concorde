package com.concorde.springboot.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Una PQRS (Petición, Queja, Reclamo o Sugerencia) que registra un
 * cliente. Es distinta del chatbot: el chatbot resuelve dudas rápidas
 * en el momento; una PQRS queda registrada con seguimiento formal
 * (estado + respuesta) hasta que un agente o administrador la cierre.
 */
@Entity
@Table(name = "pqrs")
public class Pqrs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPqrs;

    // Puede ser null si en el futuro se permite radicar sin sesión;
    // hoy el frontend siempre manda el usuario logueado.
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // PETICION | QUEJA | RECLAMO | SUGERENCIA
    private String tipo;

    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // ABIERTA | EN_PROCESO | RESUELTA
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String respuesta;

    private LocalDateTime creadoEn;
    private LocalDateTime respondidoEn;

    public Integer getIdPqrs() { return idPqrs; }
    public void setIdPqrs(Integer idPqrs) { this.idPqrs = idPqrs; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getRespondidoEn() { return respondidoEn; }
    public void setRespondidoEn(LocalDateTime respondidoEn) { this.respondidoEn = respondidoEn; }
}

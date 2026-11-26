package br.com.cftecnologia.ordens.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipamentos")
public class Equipamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String tipo;
    @Column(length = 80) private String marca;
    @Column(length = 80) private String modelo;
    @Column(name = "numero_serie", length = 100) private String numeroSerie;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    @OneToMany(mappedBy = "equipamento") private List<OrdemServico> ordens = new ArrayList<>();
    protected Equipamento() {}
    public Equipamento(String tipo, String marca, String modelo, String numeroSerie, Cliente cliente) {
        this.tipo = tipo; this.marca = marca; this.modelo = modelo; this.numeroSerie = numeroSerie; this.cliente = cliente;
    }
    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<OrdemServico> getOrdens() { return ordens; }
    @Override public String toString() {
        String identificacao = (marca == null ? "" : marca + " ") + (modelo == null ? "" : modelo);
        return tipo + " - " + identificacao.trim();
    }
}

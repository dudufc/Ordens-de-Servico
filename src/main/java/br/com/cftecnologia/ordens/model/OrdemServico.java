package br.com.cftecnologia.ordens.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @Column(name = "descricao_problema", nullable = false, length = 2000)
    private String descricaoProblema;

    @Column(length = 2000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrdem status = StatusOrdem.ABERTA;

    @Column(name = "valor_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorEstimado = BigDecimal.ZERO;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    protected OrdemServico() {
    }

    public OrdemServico(Equipamento equipamento, String descricaoProblema, BigDecimal valorEstimado) {
        this.equipamento = equipamento;
        this.descricaoProblema = descricaoProblema;
        this.valorEstimado = valorEstimado;
    }

    @PrePersist
    private void definirDataAbertura() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusOrdem getStatus() {
        return status;
    }

    public void setStatus(StatusOrdem status) {
        this.status = status;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
}


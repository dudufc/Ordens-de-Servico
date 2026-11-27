package br.com.cftecnologia.ordens.model;
import jakarta.persistence.*;
@Entity @Table(name="usuarios")
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String senha;
 @Column(nullable=false) private String perfil;
 @OneToOne(fetch=FetchType.EAGER) @JoinColumn(name="cliente_id",unique=true) private Cliente cliente;
 protected Usuario() {}
 public Usuario(String email,String senha,String perfil,Cliente cliente){this.email=email;this.senha=senha;this.perfil=perfil;this.cliente=cliente;}
 public void setEmail(String email){this.email=email;}
 public void setSenha(String senha){this.senha=senha;}
 public String getEmail(){return email;} public String getSenha(){return senha;} public String getPerfil(){return perfil;} public Cliente getCliente(){return cliente;}
}

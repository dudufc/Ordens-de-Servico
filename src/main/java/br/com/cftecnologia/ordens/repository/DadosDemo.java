package br.com.cftecnologia.ordens.repository;

import br.com.cftecnologia.ordens.model.*;
import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import java.math.BigDecimal;

/** Dados fictícios opcionais, apenas para demonstração local. */
public final class DadosDemo {
 private DadosDemo() {}
 public static void inserir() {
  var em = PersistenceManager.getFactory().createEntityManager();
  var tx = em.getTransaction();
  try {
   tx.begin();
   if (em.createQuery("select count(u) from Usuario u where u.perfil = 'ADMIN'", Long.class).getSingleResult() == 0) {
    System.out.println("Configure o administrador antes de importar os dados demo.");
    tx.rollback(); return;
   }
   String[][] dados = {
    {"Mariana Alves", "mariana@example.com", java.util.UUID.randomUUID().toString(), "Notebook", "Dell", "Inspiron 15", "Telefone", "Samsung", "Galaxy A55", "Notebook não inicia", "ABERTA"},
    {"Lucas Ribeiro", "lucas@example.com", java.util.UUID.randomUUID().toString(), "Computador", "Lenovo", "ThinkCentre", "Monitor", "LG", "24MK", "Computador reinicia sozinho", "EM_ANDAMENTO"},
    {"Camila Martins", "camila@example.com", java.util.UUID.randomUUID().toString(), "Notebook", "Acer", "Aspire 5", "Impressora", "Epson", "L3250", "Sistema lento", "CONCLUIDA"},
    {"Rafael Costa", "rafael@example.com", java.util.UUID.randomUUID().toString(), "Telefone", "Motorola", "Moto G84", "Notebook", "Asus", "Vivobook", "Falha no carregamento", "CANCELADA"}
   };
   for (int i=0;i<dados.length;i++) {
    var d=dados[i];
    if (em.createQuery("select count(u) from Usuario u where lower(u.email)=:email",Long.class).setParameter("email",d[1]).getSingleResult()>0 || em.createQuery("select count(c) from Cliente c where lower(c.email)=:email",Long.class).setParameter("email",d[1]).getSingleResult()>0) continue;
    var c=new Cliente(d[0],d[1],""); em.persist(c);
    em.persist(new Usuario(d[1],Autenticacao.hash(d[2]),"CLIENTE",c));
    var e=new Equipamento(d[3],d[4],d[5],"DEMO-"+(i+1)+"-A",c); em.persist(e);
    em.persist(new Equipamento(d[6],d[7],d[8],"DEMO-"+(i+1)+"-B",c));
    var o=new OrdemServico(e,d[9],BigDecimal.ZERO);
    o.setStatus(StatusOrdem.valueOf(d[10]));
    if(i==2)o.setObservacoes("Limpeza do sistema e atualização de drivers.");
    if(i==3)o.setObservacoes("Cancelado a pedido do cliente.");
    em.persist(o);
   }
   tx.commit(); System.out.println("Importação demo concluída; contas existentes preservadas.");
  } catch(RuntimeException ex) { if(tx.isActive())tx.rollback(); throw ex; } finally {em.close();}
 }
}


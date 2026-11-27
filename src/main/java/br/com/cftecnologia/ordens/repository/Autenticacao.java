package br.com.cftecnologia.ordens.repository;
import br.com.cftecnologia.ordens.model.*;
import br.com.cftecnologia.ordens.persistencia.PersistenceManager;
import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
public class Autenticacao {
 private static byte[] derivar(String senha,byte[] salt){
  var spec=new PBEKeySpec(senha.toCharArray(),salt,600000,256);
  try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}catch(GeneralSecurityException e){throw new IllegalStateException(e);}finally{spec.clearPassword();}
 }
 static String hash(String senha){byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);return Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(derivar(senha,salt));}
 public boolean primeiroAcesso(){return new JpaRepository<>(Usuario.class).listarTodos().isEmpty();}
 public Optional<Usuario> entrar(String email,String senha){return new JpaRepository<>(Usuario.class).listarTodos().stream().filter(u->u.getEmail().equals(email.strip().toLowerCase(Locale.ROOT))).filter(u->{var p=u.getSenha().split(":");return MessageDigest.isEqual(Base64.getDecoder().decode(p[1]),derivar(senha,Base64.getDecoder().decode(p[0])));}).findFirst();}
 public void cadastrar(String nome,String email,String senha,boolean admin){
  email=email.strip().toLowerCase(Locale.ROOT); nome=nome.strip();
  if(!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")||email.length()>120||senha.length()<8||senha.length()>128||(!admin&&(nome.isEmpty()||nome.length()>120)))throw new IllegalArgumentException("Informe dados válidos e senha de 8 a 128 caracteres.");
  var em=PersistenceManager.getFactory().createEntityManager();var tx=em.getTransaction();
  try{tx.begin();long total=em.createQuery("select count(u) from Usuario u",Long.class).getSingleResult();if(admin!=(total==0))throw new IllegalArgumentException("Perfil indisponível.");
   if(!em.createQuery("select u from Usuario u where u.email=:email",Usuario.class).setParameter("email",email).getResultList().isEmpty())throw new IllegalArgumentException("E-mail já cadastrado.");
   Cliente c=null;if(!admin){if(!em.createQuery("select c from Cliente c where lower(c.email)=:email",Cliente.class).setParameter("email",email).getResultList().isEmpty())throw new IllegalArgumentException("E-mail já vinculado a cliente. Procure o administrador.");c=new Cliente(nome,email,"");em.persist(c);}em.persist(new Usuario(email,hash(senha),admin?"ADMIN":"CLIENTE",c));tx.commit();
  }catch(RuntimeException e){if(tx.isActive())tx.rollback();throw e;}finally{em.close();}
 }
 public Usuario alterarSenha(Usuario sessao,String atual,String nova){
  if(sessao==null)throw new IllegalArgumentException("Acesso negado.");
  validarSenha(nova);
  var u=entrar(sessao.getEmail(),atual).orElseThrow(()->new IllegalArgumentException("Senha atual incorreta."));
  u.setSenha(hash(nova));return new JpaRepository<>(Usuario.class).salvar(u);
 }
 public void redefinirSenha(Usuario sessao,Long clienteId,String nova){
  if(sessao==null||!"ADMIN".equals(sessao.getPerfil()))throw new IllegalArgumentException("Acesso negado.");
  validarSenha(nova);
  var repo=new JpaRepository<>(Usuario.class);
  var u=repo.listarTodos().stream().filter(x->"CLIENTE".equals(x.getPerfil())&&x.getCliente()!=null&&Objects.equals(x.getCliente().getId(),clienteId)).findFirst().orElseThrow(()->new IllegalArgumentException("Cliente sem conta de acesso."));
  u.setSenha(hash(nova));repo.salvar(u);
 }
 private static void validarSenha(String s){if(s==null||s.length()<8||s.length()>128)throw new IllegalArgumentException("Senha deve ter de 8 a 128 caracteres.");}
}

package br.com.cftecnologia.ordens.controller;
import br.com.cftecnologia.ordens.model.Usuario;
final class Sessao {
 private static Usuario usuario;
 static void entrar(Usuario u){usuario=u;}
 static void sair(){usuario=null;}
 static boolean admin(){return usuario!=null && "ADMIN".equals(usuario.getPerfil());}
 static Usuario usuario(){return usuario;}
}

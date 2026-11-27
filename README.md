# Ordens de Serviço

Aplicação desktop para organizar atendimentos técnicos, desenvolvida como trabalho final de Linguagem de Programação III, do curso de Ciência da Computação (IFRS Campus Ibirubá).

> **Projeto em desenvolvimento.** A estrutura inicial e os CRUDs de clientes e equipamentos estão disponíveis. Ordens serão implementadas na próxima etapa.

## Problema e objetivo

Pequenas assistências técnicas precisam registrar quais equipamentos pertencem a cada cliente e acompanhar solicitações de manutenção. O projeto pretende concentrar essas informações em um único programa, com histórico e estados das ordens de serviço.

## Integrantes

- Eduardo França

## Funcionalidades planejadas

- [x] Cadastro, pesquisa, edição e exclusão de clientes.
- [x] Cadastro, pesquisa, edição e exclusão de equipamentos vinculados aos clientes.
- [ ] Abertura, listagem e acompanhamento de ordens de serviço.
- [ ] Validação de formulários e confirmação antes da exclusão.
- [ ] Filtros por cliente e status, totalizadores e indicadores de atendimento.

## Tecnologias

- Java 21 e JavaFX 21 (FXML, Controllers e CSS).
- Maven para dependências e execução.
- SQLite e JPA/Hibernate (sem JDBC direto no código da aplicação).
- JUnit 5 para testes automatizados.

## Modelo de dados

| Entidade | Campos principais | Relacionamento |
| --- | --- | --- |
| `Cliente` | Nome, e-mail, telefone | Um cliente possui vários equipamentos. |
| `Equipamento` | Tipo, marca, modelo, número de série | Pertence a um cliente e pode ter várias ordens. |
| `OrdemServico` | Problema, observações, status, valor, data | Pertence a um equipamento. |

Status possíveis: `ABERTA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.

## Estrutura de pastas

```text
src/main/java/br/com/cftecnologia/ordens/
├── MainApp.java
├── controller/     # Controllers JavaFX
├── model/          # Entidades JPA e enums
├── persistencia/   # Inicialização do EntityManagerFactory
└── repository/     # Acesso aos dados e transações
src/main/resources/
├── META-INF/persistence.xml
├── styles/app.css
└── view/main.fxml
src/test/java/      # Testes automatizados
```

## Como executar

Pré-requisitos: **JDK 21** e **Apache Maven 3.9+**. Abra a pasta do projeto em uma IDE Java ou execute, no terminal:

```bash
mvn clean javafx:run
```

Para executar os testes:

```bash
mvn test
```

O SQLite será criado automaticamente em `dados/cfservice.db` na primeira utilização da camada de persistência. A pasta `dados/` e os arquivos `.db` ficam fora do Git.

## Persistência e transações

As três entidades estão mapeadas no `persistence.xml`, com relacionamentos `@OneToMany` e `@ManyToOne`. O `JpaRepository` oferece operações básicas de consulta e gravação; escritas usam transações e rollback em caso de erro.


## Diagrama e capturas de tela

Serão incluídos na etapa de documentação em `docs/diagrama-classes.png` e `docs/imagens/`, após os cadastros estarem concluídos.



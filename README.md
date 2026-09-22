# Controle de Saída de Alunos

Sistema para agilizar e tornar mais segura a saída de alunos ao final do turno escolar, evitando que os responsáveis precisem circular pelo interior da escola ou que a portaria precise se deslocar/gritar pelos corredores para avisar cada sala.

## O problema

Atualmente, quando um responsável chega para buscar um aluno, a escola tem duas opções, ambas problemáticas:

- A portaria grita ou se desloca até a sala para avisar qual aluno deve descer — pouco eficiente e gera tumulto.
- O responsável entra na escola para buscar o aluno pessoalmente — risco de segurança para os alunos e profissionais.

Além disso, a liberação costuma ser feita em massa, sem checar se o responsável já chegou, gerando crianças aguardando sem supervisão ou saindo sozinhas antes da hora.

## A solução

O responsável chega à entrada da escola e, em um tablet disponível na portaria (o **totem**), seleciona diretamente a turma e o aluno que veio buscar. Essa informação é enviada em tempo real para a TV da sala de aula correspondente (o **painel**).

Na tela da sala, cada aluno aparece representado por um cartão colorido: azul enquanto aguarda, vermelho assim que é chamado. O professor vê a mudança e sabe imediatamente qual aluno mandar para a porta — sem interromper a aula gritando nomes, e sem liberar a turma inteira de uma vez.

## Funcionalidades

- [x] Cadastro de alunos e turmas
- [x] Cadastro de responsáveis autorizados a retirada, vinculados a alunos
- [x] Validação de que o responsável selecionado é de fato autorizado a retirar aquele aluno
- [x] Painel por turma com status em tempo real (aguardando / chamado), com lista de chamados em ordem de chegada
- [x] Autenticação com senha protegida por hash (BCrypt) e sessão por token (JWT)
- [x] Perfis de acesso diferenciados: direção, secretaria, login de sala (TV) e login de totem (tablet)
- [x] Totem da portaria, acessado via navegador, exigindo login do tablet
- [x] Painel de administração completo (criar, editar, excluir, buscar) para turmas, alunos, responsáveis e logins
- [x] Identidade visual própria (paleta educacional, marca d'água, cores por ano no totem)
- [x] Teste de carga validando o horário de pico (250–300 saídas em ~10 minutos)


## Status atual

- Em desenvolvimento, mas funcionalmente completo para uso interno/testes.

O sistema roda de ponta a ponta: cadastro pela administração, totem funcional na portaria, painel em tempo real na sala, tudo protegido por login e permissões por perfil. Já passou por um teste de carga simulando o pico real de saída da escola-piloto, sem falhas. Falta formalizar o uso de dados com a escola (LGPD) e colocar no ar em um servidor real (hoje roda localmente, em ambiente de desenvolvimento).

## Perfis de acesso

| Perfil | O que pode fazer |
|---|---|
| **Direção** (`admin`) | Acesso total: turmas, alunos, responsáveis, logins, autorizar retirada |
| **Secretaria** (`secretaria`) | Cadastra responsáveis, lista e edita alunos, autoriza/remove retiradas — não gerencia turmas nem cria logins |
| **Sala** (`sala`) | Login fixo de uma turma (a TV), vai direto para o painel daquela sala |
| **Totem** (`totem`) | Login do tablet da portaria — sem ele, não é possível chamar nenhum aluno |

## Arquitetura

O sistema é dividido em duas partes:

**Backend (Java + Spring Boot)** — concentra toda a lógica de negócio, autenticação e acesso ao banco de dados, exposta como uma API REST.

- `model` — representação dos dados (Turma, Aluno, Responsavel, RegistroSaida, Usuario)
- `dao` — acesso ao banco de dados (PostgreSQL)
- `service` — regras de negócio (autorização, bloqueio de chamadas duplicadas, autenticação, validações)
- `app` — camada web: controllers, DTOs, e a checagem de autenticação/autorização (`ContextoAutenticacao`) usada por todos os endpoints protegidos
- `config` — configuração (conexão com banco via variáveis de ambiente, geração/validação de token JWT, limitador de tentativas de login)

**Clientes**, todos consumindo a mesma API:

- **Login** (`index.html`) — ponto de entrada único; após autenticar, redireciona automaticamente para a tela certa conforme o perfil
- **Totem da portaria** (`totem.html`) — exige login do tablet; turmas agrupadas por ano, cada ano com sua cor
- **Painel da sala** (`painel.html`) — atualização automática a cada 4 segundos (polling), sem necessidade de login para visualizar
- **Administração** (`admin.html`) — navegação por abas, com seções visíveis conforme o perfil de quem está logado
- **App mobile** (futuro) — Android nativo, consumindo a mesma API

## Segurança

- Senhas protegidas com hash BCrypt, nunca armazenadas em texto puro
- Sessão via token JWT (expira em 8 horas), exigido em todos os endpoints administrativos e no totem
- Endpoints do totem/painel usados pelo responsável e pela TV permanecem públicos por design (não fazem sentido exigir login de quem não tem conta no sistema)
- Limitador de tentativas de login (bloqueio após 5 tentativas erradas, por 15 minutos)
- Proteção contra XSS na tela de administração (nomes cadastrados nunca são interpretados como HTML/código)
- Senha do banco de dados fora do código-fonte (lida de variável de ambiente)

## Tecnologias

- **Backend**: Java 21, Spring Boot, Maven
- **Banco de dados**: PostgreSQL
- **Segurança**: BCrypt (senhas), JJWT (tokens de sessão)
- **Frontend**: HTML/CSS/JS puro, servido como páginas estáticas do Spring Boot
- **App mobile**: Android nativo (Kotlin), a ser desenvolvido

## Autores

- Eliézer Evangelista Silva — Estudante de Sistemas de Informação
- Kamylla Machado Rezende — Estudante de Sistemas de Informação
- Paulo Victor Matias Ferreira — Estudante de Sistemas de Informação
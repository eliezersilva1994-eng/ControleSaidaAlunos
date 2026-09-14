# Controle de Saída de Alunos

Sistema para agilizar e tornar mais segura a saída de alunos ao final do turno escolar, evitando que os responsáveis precisem circular pelo interior da escola ou que a portaria precise se deslocar/gritar pelos corredores para avisar cada sala.

## O problema

Atualmente, quando um responsável chega para buscar um aluno, a escola tem duas opções, ambas problemáticas:

- A portaria grita ou se desloca até a sala para avisar qual aluno deve descer — pouco eficiente e gera tumulto.
- O responsável entra na escola para buscar o aluno pessoalmente — risco de segurança para os alunos e profissionais.

Além disso, a liberação costuma ser feita em massa, sem checar se o responsável já chegou:

- Em escolas com crianças mais novas, todos os alunos de uma turma costumam ser liberados juntos para a saída, independentemente de o responsável já estar lá — gerando crianças aguardando sem supervisão adequada.
- Em escolas com adolescentes, é comum que os próprios alunos saiam sozinhos antes mesmo de o responsável ter chegado.

## A solução

O responsável chega à entrada da escola e, em um tablet disponível na portaria, seleciona diretamente a turma e o aluno que veio buscar (sem depender de um funcionário para intermediar). Essa informação é enviada em tempo real para a TV da sala de aula correspondente.

Na tela da sala, cada aluno aparece representado por um quadrado. Quando o responsável dele chega e faz a seleção, o quadrado muda de cor. O professor visualiza a mudança e libera apenas aquele aluno específico para se dirigir à portaria — evitando tanto a interrupção da aula gritando nomes quanto a liberação em massa de alunos sem responsável presente.

## Funcionalidades planejadas

- [x] Cadastro de alunos e turmas
- [x] Cadastro de responsáveis autorizados a retirada, vinculados a alunos
- [x] Validação de que o responsável selecionado é de fato autorizado a retirar aquele aluno
- [x] Painel por turma com status em tempo real (aguardando / chamado / liberado)
- [x] Autenticação para professores e administração da escola (senha protegida com hash)
- [ ] Tela de autoatendimento na portaria (tablet), acessada via navegador
- [ ] Painel da sala de aula, acessado via navegador (TV comum ou smart TV)
- [ ] API REST expondo as funcionalidades acima para totem, painel e app mobile
- [ ] Aplicativo mobile para consulta/gestão pela escola

## Status atual

🚧 Em desenvolvimento.

O núcleo do sistema (modelagem de dados, regras de negócio e autenticação) está implementado e testado via linha de comando. A camada web (API REST) está em construção — a base do servidor já está no ar, e as funcionalidades estão sendo expostas como endpoints uma a uma.

## Arquitetura

O sistema é dividido em duas partes:

**Backend (Java + Spring Boot)** — concentra toda a lógica de negócio e acesso ao banco de dados, e expõe isso como uma API REST.

- `model` — representação dos dados (Turma, Aluno, Responsavel, RegistroSaida, Usuario)
- `dao` — acesso ao banco de dados (PostgreSQL)
- `service` — regras de negócio (validação de autorização, bloqueio de chamadas duplicadas, autenticação, etc.)
- `app` — camada web (Spring Boot), expõe as funcionalidades acima como endpoints HTTP

**Clientes**, todos consumindo a mesma API:

- **Totem da portaria**: página web simples, acessada pelo navegador de um tablet
- **Painel da sala de aula**: página web simples, acessada pelo navegador de uma smart TV ou de um computador conectado a uma TV comum
- **App mobile** (futuro): aplicativo Android nativo, para gestão e consulta pela escola

Essa divisão permite que a escola use os equipamentos que já possui (tablets, smart TVs, computadores), sem necessidade de hardware adicional — o acesso é sempre feito por navegador, exceto no app mobile.

## Tecnologias

- **Backend**: Java 21, Spring Boot, Maven
- **Banco de dados**: PostgreSQL
- **Segurança**: senhas protegidas com hash BCrypt
- **Frontend web** (totem e painel): a definir (HTML/JS simples)
- **App mobile**: Android nativo (Kotlin), a ser desenvolvido

## Autores

- Eliézer Evangelista Silva — Estudante de Sistemas de Informação
- Kamylla Machado Rezende — Estudante de Sistemas de Informação
- Paulo Victor Matias Ferreira — Estudante de Sistemas de Informação

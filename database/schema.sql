-- Schema do banco de dados: Controle de Saída de Alunos

CREATE TABLE turma (
                       id SERIAL PRIMARY KEY,
                       nome VARCHAR(50) NOT NULL
);

CREATE TABLE aluno (
                       id SERIAL PRIMARY KEY,
                       nome VARCHAR(150) NOT NULL,
                       turma_id INTEGER NOT NULL REFERENCES turma(id)
);

CREATE TABLE responsavel (
                             id SERIAL PRIMARY KEY,
                             nome VARCHAR(150) NOT NULL,
                             documento VARCHAR(20),
                             foto_url VARCHAR(255)
);

CREATE TABLE aluno_responsavel (
                                   aluno_id INTEGER NOT NULL REFERENCES aluno(id),
                                   responsavel_id INTEGER NOT NULL REFERENCES responsavel(id),
                                   PRIMARY KEY (aluno_id, responsavel_id)
);

CREATE TABLE registro_saida (
                                id SERIAL PRIMARY KEY,
                                aluno_id INTEGER NOT NULL REFERENCES aluno(id),
                                responsavel_id INTEGER NOT NULL REFERENCES responsavel(id),
                                horario TIMESTAMP NOT NULL DEFAULT NOW(),
                                status VARCHAR(20) NOT NULL DEFAULT 'liberado'
);
package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação web (Spring Boot).
 *
 * Isso é diferente do Main.java na raiz do projeto: aquele é um script de
 * demonstração que roda uma vez e termina; este aqui sobe um servidor web
 * que fica no ar, esperando requisições do totem, do painel da sala, etc.
 *
 * scanBasePackages lista explicitamente os pacotes model/dao/service porque
 * eles não estão "dentro" do pacote app — o Spring precisa saber onde
 * procurar as classes.
 */
@SpringBootApplication(scanBasePackages = {"app", "model", "dao", "service"})
public class SistemaSaidaAlunosApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaSaidaAlunosApplication.class, args);
    }
}
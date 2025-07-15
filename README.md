# Projeto Microservices NTT DATA
Este é um projeto de exemplo para demonstrar a arquitetura de microsserviços usando Spring Boot e Spring Cloud, com ênfase em Automação de CI/CD (Integração Contínua e Entrega Contínua) utilizando Jenkins e Docker.

### Estrutura do Projeto
Este repositório contém um projeto Maven multi-módulo. Atualmente, inclui o seguinte serviço:

* service-discovery: Um serviço de descoberta (Eureka Server) que permite que outros microsserviços se registrem e se encontrem na rede.

````bash
.
├── .github/                       # Configurações do GitHub Actions
│   └── workflows/
│       └── jenkins_trigger.yml    # Workflow para disparar e monitorar o Jenkins
├── service-discovery/             # Módulo do Eureka Server
│   ├── src/main/java/             # Código-fonte Java
│   ├── src/main/resources/        # Configurações da aplicação
│   ├── src/test/java/             # Testes unitários e de integração
│   └── pom.xml                    # POM específico do módulo
├── docker-compose.yml             # Arquivo Docker Compose para orquestração de serviços
├── Jenkinsfile                    # Definição do Pipeline CI/CD com Jenkins
└── pom.xml                        # POM principal (pai) do projeto multi-módulo
└── README.md                      # Este arquivo
````

### Tecnologias Utilizadas
* Spring Boot 3.3.1: Framework para desenvolvimento de aplicações Java baseado em microsserviços.

* Spring Cloud 2023.0.2: Ferramentas para construir sistemas distribuídos, incluindo serviço de descoberta (Eureka).

* Apache Maven: Ferramenta de automação de build e gerenciamento de dependências.

* Docker: Plataforma para desenvolver, empacotar e executar aplicações em containers.

* Docker Compose: Ferramenta para definir e executar aplicações Docker multi-container.

* Jenkins: Servidor de automação open source para CI/CD.

* GitHub Actions: Ferramenta de automação de fluxo de trabalho do GitHub para CI/CD.

* Ngrok: Utilitário para expor um servidor local à internet, facilitando a integração com serviços externos como o GitHub.

### CI/CD Pipeline (Integração Contínua e Entrega Contínua)
Este projeto implementa um pipeline de CI/CD robusto automatizado para garantir a entrega rápida e confiável do software.

#### Fluxo do Pipeline:

1. Gatilho (Push para o GitHub):

* Um git push para as branches main ou develop no GitHub dispara um GitHub Actions (.github/workflows/jenkins_trigger.yml).

2. Disparo do Jenkins (via GitHub Actions):

* O GitHub Actions, por sua vez, dispara e monitora um Pipeline Multibranch no Jenkins que está rodando localmente e exposto via Ngrok.

* O GitHub Actions aguarda o resultado do build do Jenkins para determinar seu próprio status.

3. Execução do Pipeline Jenkins (Jenkinsfile):

* Checkout do Código: Clona o código-fonte do repositório.

* Build do Eureka Server (Maven): Compila e empacota o módulo service-discovery (Eureka Server) usando Maven.

* Testes Unitários: Executa os testes unitários do service-discovery para garantir a funcionalidade em nível de componente.

* Build e Push da Imagem Docker: Constrói uma imagem Docker para o Eureka Server e a envia para o Docker Hub (cleosilva/eureka-server:<BUILD_ID_MINUSCULAS>).

* Deploy do Eureka Server: Utiliza docker-compose para parar, remover e iniciar o container do Eureka Server no ambiente de destino.

* Testes de Integração: Executa testes de integração para validar a comunicação e o comportamento do Eureka Server em um ambiente mais próximo do real (assumindo que o serviço já está em execução após o deploy).

### Como Rodar o Projeto Localmente
1. Pré-requisitos:

* Java 21 ou superior

* Maven

* Docker e Docker Compose

* Uma conta no Docker Hub

* Jenkins (localmente em um container Docker, por exemplo)

* Ngrok (para expor o Jenkins localmente)

2. Clonar o Repositório:

````Bash
git clone https://github.com/cleosilva/microservices-nttdata.git
cd microservices-nttdata
```` 
3. Subir o Jenkins (se estiver rodando via Docker):

* Certifique-se de que seu Jenkins está rodando e acessível.

* Inicie o Ngrok para expor seu Jenkins (ex: ngrok http 8888).
````bash
ngrok http 8888
````

4. Configurar o Jenkinsfile:

* Verifique o Jenkinsfile na raiz do projeto para entender os estágios.

* Certifique-se de que as credenciais do Docker Hub e GitHub estão configuradas no Jenkins.

5. Configurar o GitHub Actions:

* No seu repositório GitHub, vá em Settings > Secrets and variables > Actions 
* Adicione as secrets: 
  * JENKINS_URL, JENKINS_USER, 
  * JENKINS_API_TOKEN, 
  * JENKINS_JOB_NAME

* O arquivo .github/workflows/jenkins_trigger.yml já está configurado para disparar em push para main e develop.

6. Executar o Pipeline:

* Faça uma alteração e um git push para a branch develop (ou main).

* Observe o GitHub Actions ser executado (na aba "Actions" do seu repositório).

* Observe o pipeline do Jenkins ser disparado e executado (no seu dashboard Jenkins).

### Contato
Para dúvidas ou sugestões, por favor, abra uma issue neste repositório.
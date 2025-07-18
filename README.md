# 🚀 Microsserviços de Catálogo de Produtos e Simulador de Pedidos
Este projeto demonstra uma arquitetura de microsserviços moderna, implementada com Spring Boot e Spring Cloud. Ele foca não apenas na funcionalidade, mas também em boas práticas de desenvolvimento, organização de código, e automação de CI/CD, elementos essenciais em ambientes de desenvolvimento ágil e escalável.

## 💡 Visão Geral do Projeto
A aplicação consiste em dois microsserviços principais que interagem através de um **Service Discovery** e uma **API Gateway**, simulando um sistema de gestão de pedidos com um catálogo de produtos.

### Requisitos Obrigatórios do Desafio (Entregues)
* **Arquitetura de Microsserviços:** Dois serviços independentes desenvolvidos em Spring Boot.

* **Service Discovery:** Utilização do Spring Cloud Eureka para permitir que os serviços se localizem.

* **API Gateway:** Implementação com Spring Cloud Gateway como ponto de entrada único para todas as requisições.

* **APIs RESTful:** Adere às boas práticas de design de APIs REST.

* **Microsserviço de Catálogo de Produtos (`product-catalog`):**

  * Funcionalidades CRUD (cadastrar, listar, consultar, atualizar, deletar) para produtos (nome, descrição, preço).

  * Endpoint acessível via `/products`.

  * Observação: O requisito original pedia H2 Database, mas foi substituído por **PostgreSQL** para demonstrar persistência em ambiente de produção (detalhes no "Extras").

* **Microsserviço de Simulador de Pedidos (`order-simulator`):**

  * Consome o Microsserviço de Catálogo de Produtos para buscar itens disponíveis.

  * Permite simular a criação de um pedido com base em uma lista de IDs de produtos.

  * Não possui persistência de dados própria.

  * Endpoint acessível via `/orders`.

* **Rotas Consistentes:** Todos os endpoints são acessíveis exclusivamente via API Gateway.

  * `/products/**` roteado para `product-catalog`.

  * `/orders/**` roteado para `order-simulator`.

* **Autenticação Simplificada:** Implementada no API Gateway usando Spring Security com um filtro de token fixo (`Authorization: Bearer supersecrettoken123`).

## ✨ Extras Implementados
Para demonstrar uma compreensão mais profunda de um ambiente de desenvolvimento e deploy moderno, o projeto inclui as seguintes funcionalidades adicionais:

* **Persistência com PostgreSQL (via Docker):** Em vez de H2 (banco de dados em memória), foi configurado um banco de dados PostgreSQL, orquestrado via Docker Compose, para simular um ambiente de produção real com persistência de dados.

* **Dockerização dos Microsserviços:** Cada microsserviço (Eureka Server, Catálogo de Produtos, Simulador de Pedidos, API Gateway) possui seu próprio `Dockerfile` para encapsulamento e portabilidade.

* **Orquestração com Docker Compose:** Utilização de um docker-compose.yml abrangente para subir toda a arquitetura da aplicação (incluindo PostgreSQL, RabbitMQ e todos os microsserviços) com um único comando, facilitando a execução em qualquer ambiente.

* **CI/CD com Jenkins:**

  * Pipeline Jenkins configurada para automatizar o processo de Build, Teste e Publicação (Push) das imagens Docker para o Docker Hub.

  * Isso garante entregas contínuas e um fluxo de trabalho eficiente e testado.

* **Testes Unitários e de Integração:** Inclusão de testes para garantir a robustez e confiabilidade do código.

## 🛠️ Próximos Passos (Evolução Futura)
Este projeto é uma base sólida e continuará evoluindo com a implementação de:

* **Mensageria com RabbitMQ:** Adicionar comunicação assíncrona entre os serviços (por exemplo, para eventos de estoque ou notificações de pedido).

* **Monitoramento com Grafana e Prometheus:** Integração de ferramentas de observabilidade para coletar métricas e visualizar o desempenho da aplicação em tempo real.

## 🚀 Como Executar e Testar a Aplicação Localmente
Siga estes passos para levantar e interagir com toda a arquitetura de microsserviços em sua máquina.

### **Pré-requisitos**
Certifique-se de que você tem os seguintes softwares instalados em sua máquina:

* **Git:** Para clonar o repositório.

* **Docker Desktop (ou Docker Engine & Docker Compose):** Para construir e orquestrar os containers da aplicação.

### 1. Clonar o Repositório
   Abra seu terminal e clone o projeto:

```Bash
git clone https://github.com/cleosilva/microservices-nttdata.git
cd microservices-nttdata
```
### 2. Iniciar a Aplicação com Docker Compose
   Este comando puxará as imagens Docker dos microsserviços pré-construídas do Docker Hub (tag `latest`), configurará os serviços de banco de dados e mensageria, e iniciará toda a aplicação.

No diretório raiz do projeto (`microservices-nttdata`), execute:

````Bash
docker-compose up -d
````
* Aguarde alguns minutos para que todos os serviços sejam iniciados e registrados no Eureka Server. Você pode acompanhar o progresso com `docker-compose logs -f`.

### 3. Verificar o Status dos Serviços
   Confirme que todos os containers estão em execução:

````Bash
docker-compose ps
````
Todos os serviços (`eureka-server`, `api-gateway`, `product-catalog`, `order-simulator`, `postgres`, `rabbitmq`) devem aparecer com o status `Up`.

### 4. Acessar e Testar os Endpoints
   Agora, você pode interagir com a aplicação. O token fixo para autenticação é: `supersecrettoken123`.

#### 1. **Acessar o Painel do Eureka Server:**

* Abra seu navegador e vá para: http://localhost:8761/

* Você verá o painel do Eureka com `PRODUCT-CATALOG`, `ORDER-SIMULATOR`, e `API-GATEWAY` listados como `UP`.

#### 2. **Testar o Endpoint de Cadastro de Produto (via API Gateway):**

* Endpoint: `POST /products`

* Corpo: { "name": "Notebook Gamer", "description": "Notebook de alta performance", "price": 5000.00 }

* Headers: `Authorization: Bearer supersecrettoken123`

````Bash
curl -X POST \
-H "Content-Type: application/json" \
-H "Authorization: Bearer supersecrettoken123" \
-d '{ "name": "Notebook Gamer", "description": "Notebook de alta performance", "price": 5000.00 }' \
http://localhost:8700/products
````
#### 3. Testar o Endpoint de Listagem de Produtos (via API Gateway):

* Endpoint: `GET /products`

* Headers: `Authorization: Bearer supersecrettoken123`

````Bash
curl -X GET \
-H "Authorization: Bearer supersecrettoken123" \
http://localhost:8700/products
````
* Você deverá ver o produto cadastrado anteriormente.

#### 4. Testar a Simulação de Pedido (via API Gateway):

* Endpoint: `POST /orders/simulate`

* Corpo: `[1]` (assumindo que o produto com ID 1 foi cadastrado).

* Headers: `Authorization: Bearer supersecrettoken123`

````Bash
curl -X POST \
-H "Content-Type: application/json" \
-H "Authorization: Bearer supersecrettoken123" \
-d '[1]' \
http://localhost:8700/orders/simulate
````
* Você receberá um JSON com o pedido simulado, incluindo o item do catálogo.

### 5. Parar e Remover a Aplicação (Limpeza)
   Para derrubar todos os containers e limpar o ambiente após o teste:

No diretório raiz do projeto, execute:

```Bash
docker-compose down
````

## 🙋 Contato
### Desenvolvido por:

Cleo Silva

https://www.linkedin.com/in/cleo-silva
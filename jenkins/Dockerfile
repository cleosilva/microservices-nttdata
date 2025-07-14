# ./jenkins/Dockerfile

# Usamos a imagem base do Jenkins LTS
FROM jenkins/jenkins:lts

# Mude para o usuário root para instalar as ferramentas
USER root

# Instalar dependências básicas necessárias para curl, wget e descompactar
RUN apt-get update && \
    apt-get install -y curl wget tar gzip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Baixar e instalar o JDK 21 (Eclipse Temurin) via tar.gz
# Usando a URL direta confirmada, que inclui o nome da pasta de release correto.
ARG TEMURIN_FILENAME="OpenJDK21U-jdk_x64_linux_hotspot_21.0.3_9.tar.gz"
ARG TEMURIN_RELEASE_TAG="jdk-21.0.3%2B9"
ARG JAVA_TAR_DIR_NAME="jdk-21.0.3+9"

RUN mkdir -p /opt/java && \
    wget -q -O /tmp/jdk.tar.gz "https://github.com/adoptium/temurin21-binaries/releases/download/${TEMURIN_RELEASE_TAG}/${TEMURIN_FILENAME}" && \
    tar -xzf /tmp/jdk.tar.gz -C /opt/java && \
    rm /tmp/jdk.tar.gz

# Configurar as variáveis de ambiente JAVA_HOME e PATH para o JDK 21
ENV JAVA_HOME=/opt/java/${JAVA_TAR_DIR_NAME}
ENV PATH=$PATH:$JAVA_HOME/bin

# Instalar docker-compose V2.x (versão mais recente estável, ex: v2.27.0)
# Verifique a última versão em https://github.com/docker/compose/releases
RUN curl -L "https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# Opcional, mas recomendado: Instalar o cliente Docker CLI
RUN curl -fsSL https://get.docker.com/ | sh

# Voltar para o usuário jenkins por segurança
USER jenkins
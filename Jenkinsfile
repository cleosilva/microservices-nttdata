// Jenkinsfile
pipeline {
    agent any

    environment {
        // Defina o DOCKER_USERNAME com seu nome de usuário do Docker Hub
        DOCKER_USERNAME = 'cleosilva' // SUBSTITUA PELO SEU USUÁRIO DO DOCKER HUB
        DOCKER_REGISTRY = 'docker.io' // Geralmente docker.io para Docker Hub

        // Imagem do Eureka Server
        DOCKER_IMAGE_EUREKA = "${DOCKER_USERNAME}/eureka-server:${env.BUILD_ID}"
    }

    stages {
        stage('Checkout Source Code') {
            steps {
                git branch: 'main', url: 'https://github.com/cleosilva/microservices-nttdata.git'
            }
        }

        stage('Build Eureka Server') {
            steps {
                script {
                    // Navega para o diretório do service-discovery e constrói o JAR
                    sh "cd service-discovery && mvn clean package -DskipTests"
                }
            }
        }

        stage('Docker Build and Push Eureka Server') {
            steps {
                script {
                    // Certifique-se de que 'dockerhub-credentials' foi configurado no Jenkins
                    withDockerRegistry(credentialsId: 'dockerhub-credentials', url: "${DOCKER_REGISTRY}") {
                        // Constrói a imagem Docker a partir do Dockerfile em service-discovery/
                        docker.build("${DOCKER_IMAGE_EUREKA}", "./service-discovery").push()
                    }
                }
            }
        }

        stage('Deploy Eureka Server') {
            steps {
                script {
                    // Primeiro, pare e remova o container atual do Eureka para garantir um deploy limpo
                    // '|| true' para que o comando não falhe se o container não existir
                    sh "docker-compose stop eureka-server || true"
                    sh "docker-compose rm -f eureka-server || true"

                    // Inicia o container do Eureka Server com a imagem mais recente
                    // O --build garantirá que se houver alterações no Dockerfile local, ele reconstrua
                    // Se você quer que ele puxe a imagem do Docker Hub, você precisaria mudar o docker-compose.yml
                    // para usar 'image: ${DOCKER_USERNAME}/eureka-server' e fazer um 'docker pull' antes.
                    // Por enquanto, 'docker-compose up --build -d eureka-server' é simples e eficaz.
                    sh "docker-compose up -d --build eureka-server"
                }
            }
        }

        stage('Run Tests (Eureka Server)') {
            steps {
                script {
                    // Para o Eureka Server, testes unitários são suficientes por agora.
                    // Testes de integração complexos envolveriam verificar se outros serviços conseguem se registrar, etc.
                    sh "cd service-discovery && mvn test"
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
    }
}
// Jenkinsfile
pipeline {
    agent any
    tools {
       maven 'Maven 3.9.6'
    }

    environment {
        // Defina o DOCKER_USERNAME com seu nome de usuário do Docker Hub
        DOCKER_USERNAME = 'cleosilva' // SUBSTITUA PELO SEU USUÁRIO DO DOCKER HUB
        DOCKER_REGISTRY = 'https://docker.io' // Geralmente docker.io para Docker Hub

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
                sh "cd service-discovery && mvn clean package -DskipTests"
            }
        }

        stage('Docker Build and Push Eureka Server') {
            steps {
                script{
                    withDockerRegistry(credentialsId: 'dockerhub-credentials', url: "${DOCKER_REGISTRY}") {
                        sh "docker build -t ${DOCKER_IMAGE_EUREKA} ./service-discovery"
                        sh "docker push ${DOCKER_IMAGE_EUREKA}"
                    }
                }

            }
        }

        stage('Deploy Eureka Server') {
            steps {
               // Primeiro, pare e remova o container atual do Eureka para garantir um deploy limpo
               // '|| true' para que o comando não falhe se o container não existir
               sh "docker-compose stop eureka-server || true"
               sh "docker-compose rm -f eureka-server || true"

               // Inicia o container do Eureka Server com a imagem mais recente
               sh "docker-compose up -d --build eureka-server"

            }
        }

        stage('Run Tests (Eureka Server)') {
            steps {
               // Para o Eureka Server, testes unitários são suficientes por agora.
               sh "cd service-discovery && mvn test"
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
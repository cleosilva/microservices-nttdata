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
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                    }
                }

            }
        }

        stage('Deploy Eureka Server') {
            steps {
                script {
                    // Converte BUILD_ID para minúsculas para a tag da imagem Docker
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase()

                    // Assegura que BUILD_ID (agora em minúsculas) esteja disponível para o docker-compose
                    // Usamos a variável lowerCaseBuildId para a tag
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose stop eureka-server || true"
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose rm -f eureka-server || true"
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose up -d --build eureka-server"
                }
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
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
                script {
                    // Garante que a variável BUILD_ID esteja em minúsculas para a tag
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase()

                    // Autenticação no Docker Hub (isso já está funcionando)
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin"
                    }

                    // Construir a imagem Docker
                    // O Dockerfile para o eureka-server está dentro de service-discovery
                    // Usamos a tag com o BUILD_ID em minúsculas
                    sh "docker build -t cleosilva/eureka-server:${lowerCaseBuildId} ./service-discovery"

                    // Fazer o push da imagem para o Docker Hub
                    sh "docker push cleosilva/eureka-server:${lowerCaseBuildId}"
                }
            }
        }

        // ... (agora o estágio de Deploy Eureka Server pode referenciar essa imagem) ...

        stage('Deploy Eureka Server') {
            steps {
                script {
                    def lowerCaseBuildId = env.BUILD_ID.toLowerCase() // Re-declarar ou passar da etapa anterior

                    // Como a imagem já foi construída e "pushed" (enviada),
                    // o docker-compose agora poderá encontrá-la.
                    // Não precisamos do --build aqui, pois a imagem já existe no registro.
                    // Mas vamos mantê-lo para garantir que, se por algum motivo não achasse no registro, ele tentasse localmente.
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose stop eureka-server || true"
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose rm -f eureka-server || true"
                    sh "BUILD_ID=${lowerCaseBuildId} docker-compose up -d eureka-server" // Removido --build para ser mais claro
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
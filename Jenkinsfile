
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_BIN = 'C:\\Users\\kunal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        KUBECTL_BIN = 'C:\\Users\\kunal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\kubectl.exe'

        DISCOVERY_IMAGE = 'banking-discovery-service:latest'
        GATEWAY_IMAGE = 'banking-api-gateway:latest'
        AUTH_IMAGE = 'banking-authentication-service:latest'
        ACCOUNT_IMAGE = 'banking-account-service:latest'
        CUSTOMER_IMAGE = 'banking-customer-service:latest'
    }

    stages {
        stage('1 - Verify Tools and Cluster') {
            steps {
                bat '"%DOCKER_BIN%" version'
                bat '"%KUBECTL_BIN%" config current-context'
                bat '"%KUBECTL_BIN%" cluster-info'
            }
        }

        stage('2 - Build DiscoveryService') {
            steps {
                dir('DiscoveryService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat '"%DOCKER_BIN%" build -t "%DISCOVERY_IMAGE%" DiscoveryService'
            }
        }

        stage('3 - Build ApiGateway') {
            steps {
                dir('ApiGateway') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat '"%DOCKER_BIN%" build -t "%GATEWAY_IMAGE%" ApiGateway'
            }
        }

        stage('4 - Build AuthenticationService') {
            steps {
                dir('AuthenticationService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat '"%DOCKER_BIN%" build -t "%AUTH_IMAGE%" AuthenticationService'
            }
        }

        stage('5 - Build AccountService') {
            steps {
                dir('AccountService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat '"%DOCKER_BIN%" build -t "%ACCOUNT_IMAGE%" AccountService'
            }
        }

        stage('6 - Build CustomerService') {
            steps {
                dir('CustomerService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat '"%DOCKER_BIN%" build -t "%CUSTOMER_IMAGE%" CustomerService'
            }
        }

        stage('7 - Deploy MySQL Configuration') {
            steps {
                bat '"%KUBECTL_BIN%" apply -f k8s/01-mysql.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/mysql --timeout=180s'
            }
        }

        stage('8 - Deploy DiscoveryService') {
            steps {
                bat '"%KUBECTL_BIN%" apply -f k8s/02-discovery.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/discovery-service --timeout=180s'
            }
        }

        stage('9 - Deploy Backend Services') {
            steps {
                bat '"%KUBECTL_BIN%" apply -f k8s/03-backend-services.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/authentication-service --timeout=180s'
                bat '"%KUBECTL_BIN%" rollout status deployment/account-service --timeout=180s'
                bat '"%KUBECTL_BIN%" rollout status deployment/customer-service --timeout=180s'
                bat '"%KUBECTL_BIN%" rollout status deployment/api-gateway --timeout=180s'
            }
        }

        stage('10 - Verify Deployments') {
            steps {
                bat '"%KUBECTL_BIN%" get deployments,services,pods'
                bat '"%DOCKER_BIN%" images'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check Console Output before retrying.'
        }
        always {
            echo 'Banking microservices pipeline finished.'
        }
    }
}

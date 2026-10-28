
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        DISCOVERY_IMAGE = 'banking-discovery-service:latest'
        GATEWAY_IMAGE = 'banking-api-gateway:latest'
        AUTH_IMAGE = 'banking-authentication-service:latest'
        ACCOUNT_IMAGE = 'banking-account-service:latest'
        CUSTOMER_IMAGE = 'banking-customer-service:latest'
    }

    stages {
        stage('Checkout and Verify Tools') {
            steps {
                checkout scm
                bat 'git --version'
                bat 'docker version'
                bat 'kubectl config current-context'
                bat 'kubectl cluster-info'
            }
        }

        stage('1 - Build DiscoveryService') {
            steps {
                dir('DiscoveryService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat 'docker build -t banking-discovery-service:latest DiscoveryService'
            }
        }

        stage('2 - Build ApiGateway') {
            steps {
                dir('ApiGateway') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat 'docker build -t banking-api-gateway:latest ApiGateway'
            }
        }

        stage('3 - Build AuthenticationService') {
            steps {
                dir('AuthenticationService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat 'docker build -t banking-authentication-service:latest AuthenticationService'
            }
        }

        stage('4 - Build AccountService') {
            steps {
                dir('AccountService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat 'docker build -t banking-account-service:latest AccountService'
            }
        }

        stage('5 - Build CustomerService') {
            steps {
                dir('CustomerService') {
                    bat 'mvnw.cmd clean package -DskipTests'
                }
                bat 'docker build -t banking-customer-service:latest CustomerService'
            }
        }

        stage('Deploy MySQL Configuration') {
            steps {
                bat 'kubectl apply -f k8s/01-mysql.yaml'
                bat 'kubectl rollout status deployment/mysql --timeout=180s'
            }
        }

        stage('Deploy DiscoveryService') {
            steps {
                bat 'kubectl apply -f k8s/02-discovery.yaml'
                bat 'kubectl rollout status deployment/discovery-service --timeout=180s'
            }
        }

        stage('Deploy Backend Services') {
            steps {
                bat 'kubectl apply -f k8s/03-backend-services.yaml'
                bat 'kubectl rollout status deployment/authentication-service --timeout=180s'
                bat 'kubectl rollout status deployment/customer-service --timeout=180s'
                bat 'kubectl rollout status deployment/account-service --timeout=180s'
                bat 'kubectl rollout status deployment/api-gateway --timeout=180s'
            }
        }
    }

    post {
        success {
            echo 'Build and deployment stages completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check Console Output before retrying.'
        }
        always {
            echo 'Banking microservices pipeline finished.'
        }
    }
}

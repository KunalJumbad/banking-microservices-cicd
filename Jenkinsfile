pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21.0.12.1'
        DOCKER_BIN = 'C:\\Users\\kunal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        KUBECTL_BIN = 'C:\\Users\\kunal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\kubectl.exe'
        KUBECONFIG = 'C:\\ProgramData\\Jenkins\\.jenkins\\.kube\\config'

        DISCOVERY_IMAGE = 'banking-discovery-service:latest'
        GATEWAY_IMAGE = 'banking-api-gateway:latest'
        AUTH_IMAGE = 'banking-authentication-service:latest'
        ACCOUNT_IMAGE = 'banking-account-service:latest'
        CUSTOMER_IMAGE = 'banking-customer-service:latest'
    }

    stages {
        stage('1. Checkout') {
            steps {
                checkout scm
            }
        }

        stage('2. Build JARs') {
            steps {
                dir('DiscoveryService') {
                    bat 'set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\\bin;%PATH%" && mvnw.cmd clean package -DskipTests'
                }
                dir('ApiGateway') {
                    bat 'set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\\bin;%PATH%" && mvnw.cmd clean package -DskipTests'
                }
                dir('AuthenticationService') {
                    bat 'set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\\bin;%PATH%" && mvnw.cmd clean package -DskipTests'
                }
                dir('AccountService') {
                    bat 'set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\\bin;%PATH%" && mvnw.cmd clean package -DskipTests'
                }
                dir('CustomerService') {
                    bat 'set "JAVA_HOME=%JAVA_HOME%" && set "PATH=%JAVA_HOME%\\bin;%PATH%" && mvnw.cmd clean package -DskipTests'
                }
            }
        }

        stage('3. Build Images') {
            steps {
                bat '"%DOCKER_BIN%" build -t %DISCOVERY_IMAGE% DiscoveryService'
                bat '"%DOCKER_BIN%" build -t %GATEWAY_IMAGE% ApiGateway'
                bat '"%DOCKER_BIN%" build -t %AUTH_IMAGE% AuthenticationService'
                bat '"%DOCKER_BIN%" build -t %ACCOUNT_IMAGE% AccountService'
                bat '"%DOCKER_BIN%" build -t %CUSTOMER_IMAGE% CustomerService'
                bat '"%DOCKER_BIN%" images "banking-*"'
            }
        }

        stage('4. Deploy Stack') {
            steps {
                bat '"%KUBECTL_BIN%" apply -f k8s/01-mysql.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/mysql --timeout=180s'

                bat '"%KUBECTL_BIN%" apply -f k8s/02-discovery.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/discovery-service --timeout=180s'

                bat '"%KUBECTL_BIN%" apply -f k8s/03-api-gateway.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/api-gateway --timeout=180s'

                bat '"%KUBECTL_BIN%" apply -f k8s/04-authentication-service.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/authentication-service --timeout=180s'

                bat '"%KUBECTL_BIN%" apply -f k8s/05-account-service.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/account-service --timeout=180s'

                bat '"%KUBECTL_BIN%" apply -f k8s/06-customer-service.yaml'
                bat '"%KUBECTL_BIN%" rollout status deployment/customer-service --timeout=180s'

                bat '"%KUBECTL_BIN%" get deployments,services,pods'
            }
        }

        stage('Post Actions') {
            steps {
                echo '============================================================='
                echo 'MAIN PIPELINE PASSED: Microservices Deployed Successfully!'
                echo '============================================================='
                echo 'Eureka Service Registry: http://localhost:8761'
                echo 'API Gateway: http://localhost:8765'
                echo 'Auth Service: http://localhost:9898'
                echo 'Customer Service: http://localhost:8081'
                echo 'Account Service: http://localhost:8082'
                echo 'Frontend Portal: http://localhost:3000'
                echo 'Jenkins (Safe & Active): http://localhost:8080'
                echo '============================================================='
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

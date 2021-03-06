pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-e TZ="Asia/Shanghai" -v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test -Dmaven.test.failure.ignore=false'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn install -DskipTests'
            }
        }
        stage('SonarQube analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarQube Scanner'

                    withSonarQubeEnv('SonarQube') {
                        sh "${scannerHome}/bin/sonar-scanner -Dproject.settings=sonar-project.properties"
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sshPublisher(
                        continueOnError: false, failOnError: true,
                        publishers: [
                                sshPublisherDesc(
                                        configName: "vivo",
                                        verbose: true,
                                        transfers: [
                                                sshTransfer(
                                                        sourceFiles: "**/backend-0.0.1-SNAPSHOT.jar",
                                                        removePrefix: "target",
                                                ),
                                                sshTransfer(
                                                        sourceFiles: "**/deploy.sh",
                                                        execCommand: "chmod 755 deploy.sh && bash deploy.sh"
                                                ),
                                        ]
                                )
                        ]
                )
            }
        }
    }
}
#!/usr/bin/env groovy

pipeline {
    agent any

    post {
        success {
            ircSendSuccess()
        }

        failure {
            ircSendFailure()
        }
    }

    stages {
        stage('Checkout') {
            steps {
                ircSendStarted()

                checkout scm
            }
        }

        stage('Build') {
            steps {
                def mvnHome = tool 'M3'

                sh "${mvnHome}/bin/mvn -Dmaven.test.failure.ignore clean package"
            }
        }

        stage('Archive') {
            steps {
                archive includes: 'target/*.jar,target/*.hpi'
                junit '**/target/surefire-reports/TEST-*.xml'
            }
        }
    }
}

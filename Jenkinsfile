pipeline {
    agent any
    stages {
        stage('Checkout') {
            agent {
                label 'linuxbuild'
            }
            steps {
                git branch: 'docker-on-jenkins', url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'
            }
        }
        stage('Build linux') {
            agent {
                dockerfile true
            }
            steps {
                sh 'scripts/build_linux.sh'
                sh 'scripts/package.sh'
                sh 'cp webrtc*.zip /out'
                archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
            }
        }
        stage('Build macOS') {
            agent {
                label 'built-in'
            }
            steps {
                sh './scripts/build_iosx.sh'
                sh './scripts/package.sh'
                archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
            }
        }
    }
}

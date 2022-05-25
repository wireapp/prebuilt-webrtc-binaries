pipeline {
    agent none
    stages {
        stage('Build linux') {
            agent {
                label 'linuxbuild'
            }
            steps {
                git branch: 'docker-on-jenkins', url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'
                sh 'docker build . -t prebuilt-webrtc:snapshot'
                sh 'docker run --volume=$WORKSPACE:/out -t prebuilt-webrtc:snapshot /bin/bash -c "scripts/build_linux.sh; scripts/package.sh; cp *.zip /out"'
                archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
            }
        }
        /*
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
        */
    }
}

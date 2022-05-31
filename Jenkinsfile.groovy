pipeline {
    agent none
    stages {
        stage('Build') {
            parallel {
                stage('Linux') {
                    agent {
                        label 'linuxbuild'
                    }
                    steps {
                        git branch: 'docker-on-jenkins', url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'
                        sh 'docker container prune -f && docker volume prune -f'
                        sh 'docker build . -t prebuilt-webrtc:snapshot'
                        sh 'docker run --volume=$WORKSPACE:/out -t prebuilt-webrtc:snapshot /bin/bash -c "scripts/build_linux.sh; scripts/package.sh; cp *.zip /out"'
                        archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
                    }
                }
                stage('macOS') {
                    agent {
                        label 'built-in'
                    }
                    steps {
                        git branch: 'docker-on-jenkins', url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'
                        sh 'rm -rf ./webrtc_checkout/'
                        sh './scripts/build_iosx.sh'
                        sh './scripts/package.sh'
                        archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
                    }
                }
            }
        }
    }
}

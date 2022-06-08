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
                        // checkout
                        git branch: params.BRANCH, url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'

                        // cleanup docker
                        sh 'docker container prune -f && docker volume prune -f'

                        // build
                        sh 'docker build . -t prebuilt-webrtc'
                        sh 'docker run --volume=$WORKSPACE:/out -t --name=prebuilt-webrtc$BUILD_NUMBER -e "BUILD_NUMBER=$BUILD_NUMBER" prebuilt-webrtc /bin/bash -c "scripts/build_linux.sh; scripts/package.sh; cp *.zip /out"'
                        archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
                    }
                }
                stage('macOS') {
                    agent {
                        label 'built-in'
                    }
                    steps {
                        // checkout
                        git branch: params.BRANCH, url: 'https://github.com/wireapp/prebuilt-webrtc-binaries.git'

                        // cleanup old directories and zips
                        sh returnStatus: true, script: 'rm -rf ./webrtc_checkout/ ./depot_tools/'
                        sh returnStatus: true, script: 'rm -rf webrtc*.zip'

                        // build
                        sh './scripts/build_iosx.sh'
                        sh './scripts/package.sh'
                        archiveArtifacts artifacts: 'webrtc*.zip', followSymlinks: false
                    }
                }
            }
        }
    }
    post {
        always {
            node('linuxbuild') {
                script {
                    sh returnStatus: true, script: 'docker stop prebuilt-webrtc$BUILD_NUMBER'
                    sh 'docker container prune -f && docker volume prune -f && docker image prune -f'
                }
            }
        }
    }
}

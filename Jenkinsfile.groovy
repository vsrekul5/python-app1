pipeline{
    agent any
    environment{
        registry = 'vsrekul/pyappeploy'
        dockerImage = ''
        registryCredentials = 'dh_id'
    }
    stages{
        stage('Build'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/vsrekul5/python-app.git']]])                                
            }
        }
        stage('create image'){
            steps{
                script{
                    dockerImage = docker.build registry
                }                
            }                
        }
        stage('upload the image to the Dockr Hub'){
            steps{
                script{
                    docker.withRegistry('', registryCredentials){
                    dockerImage.push()
                    }                
                } 
            }
        }
        stage('stop container'){
            steps{
                script{
                    sh 'docker ps -f name=pyappeployContainer -q | xargs --no-run-if-empty docker container stop'
                    sh 'docker container ls -a -fname=pyappeployContainer -q | xargs -r docker container rm'
                }                
               
            }
        }
        stage('Run the app in a docker container'){
            steps{
                script{
                    dockerImage.run("-p 8096:5000 --rm --name pyappeployContainer")
                }
                
            }
        }
    }  
}
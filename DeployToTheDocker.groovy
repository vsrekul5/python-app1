pipeline{
    agent any
    environment{
        dockerImage = ''
        registry = 'vsrekul/mypyapp'
        registrycredential = 'dh_id'
    }
    stages{
        stage('Checkout'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'dh_id', url: 'https://github.com/vsrekul5/python-app.git']]])
            }
        }
        stage('Build docker Image'){
            steps{
                script{                    
                    dockerImage = docker.build registry
                }
            }
        } 
        stage('Push image'){
            steps{
                script{
                    docker.withRegistry('', registrycredential){
                        dockerImage.push()
                    }
                }
            }
        }
        stage('docker stop container') {
            steps {
               sh 'docker ps -f name=mypyappContainer -q | xargs --no-run-if-empty docker container stop'
               sh 'docker container ls -a -fname=mypyappContainer -q | xargs -r docker container rm'
         }
       } 
       stage('Run the app on docker'){
           steps{
               script{
                   dockerImage.run("-p 8096:5000 --rm --name mypyappContainer")
               }
           }
       }     
    }
}
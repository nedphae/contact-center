def getServer(ip){
    def remote = [:]
    remote.name = "server-${ip}"
    remote.host = ip
    remote.port = 22
    remote.allowAnyHosts = true
    withCredentials([usernamePassword(credentialsId: 'df2d4f43-743c-417b-b730-54c9649e39b4', passwordVariable: 'password', usernameVariable: 'userName')]) {
        remote.user = "${userName}"
        remote.password = "${password}"
    }
    return remote
}

def sendFile(sshServer, file) {
    sshPut remote: sshServer, from: file.form, into: file.into
}

def runDockerCompose(sshServer){
    sshCommand remote: sshServer, command: "cd /root/"
    sshCommand remote: sshServer, command: "docker-compose stop"
    sshCommand remote: sshServer, command: "docker-compose rm -f"
    sshCommand remote: sshServer, command: "docker rmi 192.168.170.210:5000/contact-center"
    sshCommand remote: sshServer, command: "docker-compose up -d"
}
node{
    stage('拉取代码'){
         git branch: 'develop', credentialsId: 'b51a1de4-2aa4-4c16-b70f-5305cf4d789c', url: 'http://10.100.0.107/kefuSys/webim.git'
         // checkout scm
    }

    stage('gradle 构建') {
        // build and test
        sh 'chmod +x ./gradlew'
        sh './gradlew contact-center:bootJar'
    }

    stage("复制文件") {
        def sshServer = getServer("192.168.170.210")
        sshCommand remote: sshServer, command: "cp /data/server/jenkins/workspace/webim-218/contact-center/docker/dev/Dockerfile /data/server/jenkins/workspace/webim-218/contact-center/Dockerfile"
    }

    stage("构建镜像") {
        // working with docker-ce
        withDockerServer([uri: 'tcp://192.168.170.210:2375']) {
            def customImage = docker.build("contact-center:latest", "contact-center")
        }
    }

    stage("pull docker image") {
        def sshServer = getServer("192.168.170.210")
        sshCommand remote: sshServer, command: "docker tag contact-center:latest localhost:5000/contact-center:latest"
        sshCommand remote: sshServer, command: "docker push localhost:5000/contact-center:latest"
        sshCommand remote: sshServer, command: "docker rmi contact-center:latest"
    }

    stage("send docker-compose file") {
        def file = [:]
        file.form = 'docker/docker-compose.yml'
        file.into = 'docker-compose.yml'
        def sshServer = getServer("192.168.101.218")
        sendFile(sshServer, file)
     }

    stage("run docker-compose") {
        def sshServer = getServer("192.168.101.218")
        runDockerCompose(sshServer)
    }
}
import groovy.json.JsonOutput

def call(String state, String description, String context = 'jenkins/ci') {
    if (!env.GITHUB_REPO || !env.GIT_COMMIT) {
        echo 'githubStatus: falta GITHUB_REPO o GIT_COMMIT, no se notifica'
        return
    }
    try {
        withCredentials([string(credentialsId: 'github-token', variable: 'GH_TOKEN')]) {
            httpRequest(
                url: "https://api.github.com/repos/${env.GITHUB_REPO}/statuses/${env.GIT_COMMIT}",
                httpMode: 'POST',
                contentType: 'APPLICATION_JSON',
                customHeaders: [
                    [name: 'Authorization', value: "Bearer ${GH_TOKEN}", maskValue: true],
                    [name: 'Accept', value: 'application/vnd.github+json']
                ],
                requestBody: JsonOutput.toJson([
                    state      : state,
                    context    : context,
                    description: description.take(140),
                    target_url : env.BUILD_URL
                ]),
                validResponseCodes: '200:299',
                quietResponse: true
            )
        }
        echo "GitHub notificado: ${state}"
    } catch (err) {
        echo "No se pudo notificar a GitHub: ${err.message}"
    }
}

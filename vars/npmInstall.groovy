def call(Map config = [:]) {
    def flags = config.get('flags', '--prefer-offline --no-audit --no-fund')
    echo "Cache de npm: ${env.npm_config_cache ?: 'por defecto'}"
    run "npm ci ${flags}"
}

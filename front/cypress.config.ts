import { defineConfig } from 'cypress'

export default defineConfig({
  videosFolder: 'cypress/videos',
  screenshotsFolder: 'cypress/screenshots',
  fixturesFolder: 'cypress/fixtures',
  video: false,
  e2e: {
    baseUrl: 'http://localhost:4200',
    setupNodeEvents(on, config) {
      require('@cypress/code-coverage/task')(on, config)
      return config
    }
  },
})
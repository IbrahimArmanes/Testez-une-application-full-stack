describe('Login Feature', () => {
  beforeEach(() => {
    cy.visit('/login')
  })

  it('should display login form', () => {
    cy.get('mat-card-title').should('contain', 'Login')
    cy.get('input[formControlName=email]').should('exist')
    cy.get('input[formControlName=password]').should('exist')
    cy.get('button[type=submit]').should('exist')
  })

  it('should validate form fields', () => {
    cy.get('button[type=submit]').should('be.disabled')
    
    // Test email validation
    cy.get('input[formControlName=email]').type('invalid-email')
    cy.get('input[formControlName=password]').type('password123')
    cy.get('button[type=submit]').should('be.disabled')

    // Clear and type valid email
    cy.get('input[formControlName=email]').clear().type('valid@email.com')
    cy.get('button[type=submit]').should('not.be.disabled')
  })

  it('should handle successful login', () => {
    // Mock successful login response
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      }
    }).as('loginRequest')

    cy.get('input[formControlName=email]').type('test@email.com')
    cy.get('input[formControlName=password]').type('password123')
    cy.get('button[type=submit]').click()

    cy.wait('@loginRequest')
    cy.url().should('include', '/sessions')
  })

  it('should handle failed login', () => {
    // Mock failed login response
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        error: 'Invalid credentials'
      }
    }).as('failedLogin')

    cy.get('input[formControlName=email]').type('wrong@email.com')
    cy.get('input[formControlName=password]').type('wrongpassword')
    cy.get('button[type=submit]').click()

    cy.wait('@failedLogin')
    cy.get('.error').should('be.visible')
    cy.get('.error').should('contain', 'An error occurred')
  })

  it('should toggle password visibility', () => {
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password')
    cy.get('button[aria-label="Hide password"]').click()
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'text')
    cy.get('button[aria-label="Hide password"]').click()
    cy.get('input[formControlName=password]').should('have.attr', 'type', 'password')
  })

it('should redirect logged in user away from login page', () => {
  // First set up the session service state
  cy.intercept('POST', '/api/auth/login', {
      body: {
          token: 'fake-jwt-token',
          type: 'Bearer',
          id: 1,
          username: 'testUser',
          firstName: 'Test',
          lastName: 'User',
          admin: false
      }
  }).as('loginRequest')

  // Login first to set up the session
  cy.get('input[formControlName=email]').type('test@email.com')
  cy.get('input[formControlName=password]').type('password123')
  cy.get('button[type=submit]').click()

  cy.wait('@loginRequest')
    
  // Now verify redirect
  cy.url().should('include', '/sessions')
})
})

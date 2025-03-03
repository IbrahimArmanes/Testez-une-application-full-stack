describe('Register Feature', () => {
  beforeEach(() => {
    cy.visit('/register')
  })

  it('should display register form', () => {
    cy.get('mat-card-title').should('contain', 'Register')
    cy.get('input[formControlName=firstName]').should('exist')
    cy.get('input[formControlName=lastName]').should('exist')
    cy.get('input[formControlName=email]').should('exist')
    cy.get('input[formControlName=password]').should('exist')
    cy.get('button[type=submit]').should('exist')
  })

  it('should validate form fields', () => {
    cy.get('button[type=submit]').should('be.disabled')
    
    // Fill form with invalid data
    cy.get('input[formControlName=firstName]').type('Jo')
    cy.get('input[formControlName=lastName]').type('Do')
    cy.get('input[formControlName=email]').type('invalid-email')
    cy.get('input[formControlName=password]').type('12')
    
    cy.get('button[type=submit]').should('be.disabled')
  })

  it('should successfully register a new user', () => {
    // Intercept API call
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('registerRequest')

    // Fill form with valid data
    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('john.doe@example.com')
    cy.get('input[formControlName=password]').type('password123')

    cy.get('button[type=submit]').click()

    // Verify API call
    cy.wait('@registerRequest').its('request.body').should('deep.equal', {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@example.com',
      password: 'password123'
    })

    // Verify navigation to login page
    cy.url().should('include', '/login')
  })

  it('should handle registration error', () => {
    // Intercept API call with error
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: {}
    }).as('registerError')

    // Fill form
    cy.get('input[formControlName=firstName]').type('John')
    cy.get('input[formControlName=lastName]').type('Doe')
    cy.get('input[formControlName=email]').type('john.doe@example.com')
    cy.get('input[formControlName=password]').type('password123')

    cy.get('button[type=submit]').click()

    // Verify error message
    cy.get('.error').should('be.visible')
    cy.get('.error').should('contain', 'An error occurred')
  })

})

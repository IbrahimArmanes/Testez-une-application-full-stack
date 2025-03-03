describe('Logout spec', () => {
  beforeEach(() => {
    cy.visit('/login')

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
        token: 'fake-jwt-token'
      },
    })

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
    cy.url().should('include', '/sessions')
  })

  it('Should successfully logout and redirect to home page', () => {
    cy.get('span').contains('Logout').click()
    cy.url().should('eq', 'http://localhost:4200/')
  })

  it('Should not allow access to protected routes after logout', () => {
    cy.get('span').contains('Logout').click()
    
    cy.visit('/sessions')
    cy.url().should('include', '/login')
    
    cy.visit('/me')
    cy.url().should('include', '/login')
  })

  it('Should show login/register links after logout', () => {
    cy.get('span').contains('Logout').click()
    cy.get('span').contains('Login').should('be.visible')
    cy.get('span').contains('Register').should('be.visible')
  })
})
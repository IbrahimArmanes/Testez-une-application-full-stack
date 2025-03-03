describe('User Profile', () => {
  describe('Authentication', () => {
    it('should redirect to login when not authenticated', () => {
      cy.visit('/me')
      cy.url().should('include', '/login')
    })

    it('should access profile when authenticated', () => {
      // Mock user data
      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'john@example.com',
          firstName: 'John',
          lastName: 'Doe',
          admin: false,
          createdAt: '2023-01-01T00:00:00.000Z',
          updatedAt: '2023-01-02T00:00:00.000Z'
        }
      })
      cy.login();
      cy.visit('/me')
      cy.url().should('include', '/me')
    })
  })

  describe('Profile Display', () => {
    beforeEach(() => {
      // Setup authenticated state
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'testUser',
          firstName: 'John',
          lastName: 'Doe',
          admin: false
        }
      })
    })

    it('should display user information correctly', () => {
      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'john@example.com',
          firstName: 'John',
          lastName: 'Doe',
          admin: false,
          createdAt: '2023-01-01T00:00:00.000Z',
          updatedAt: '2023-01-02T00:00:00.000Z'
        }
      })

      cy.visit('/me')
      cy.contains('Name: John DOE')
      cy.contains('Email: john@example.com')
      cy.contains('Create at:')
      cy.contains('Last update:')
    })

    it('should show admin badge for admin users', () => {
      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'admin@example.com',
          firstName: 'Admin',
          lastName: 'User',
          admin: true,
          createdAt: '2023-01-01T00:00:00.000Z',
          updatedAt: '2023-01-02T00:00:00.000Z'
        }
      })

      cy.visit('/me')
      cy.contains('You are admin')
      cy.get('button').contains('delete').should('not.exist')
    })
  })

  describe('Account Management', () => {
    it('should handle account deletion for non-admin users', () => {
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'testUser',
          firstName: 'John',
          lastName: 'Doe',
          admin: false
        }
      })

      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'john@example.com',
          firstName: 'John',
          lastName: 'Doe',
          admin: false,
          createdAt: '2023-01-01T00:00:00.000Z',
          updatedAt: '2023-01-02T00:00:00.000Z'
        }
      })

      cy.intercept('DELETE', '/api/user/1', {
        statusCode: 200
      })

      cy.visit('/me')
      cy.get('button').contains('delete').click()
      cy.contains('Your account has been deleted !')
      cy.url().should('equal', Cypress.config().baseUrl + '/')
    })
  })

  describe('Navigation', () => {
    it('should navigate back when clicking back button', () => {
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'testUser',
          firstName: 'John',
          lastName: 'Doe',
          admin: false
        }
      })

      cy.intercept('GET', '/api/user/1', {
        body: {
          id: 1,
          email: 'john@example.com',
          firstName: 'John',
          lastName: 'Doe',
          admin: false,
          createdAt: '2023-01-01T00:00:00.000Z',
          updatedAt: '2023-01-02T00:00:00.000Z'
        }
      })

      cy.visit('/me')
      cy.get('button mat-icon').contains('arrow_back').click()
    })
  })
})

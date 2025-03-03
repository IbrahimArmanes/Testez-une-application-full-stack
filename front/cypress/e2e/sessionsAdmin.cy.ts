describe('Sessions Management Admin', () => {
    beforeEach(() => {
      cy.visit('/login')
  
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true
        },
      })
  
      cy.intercept('GET', '/api/auth/me', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: true
        }
      }).as('me')
  
      cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('sessions')
  
      cy.get('input[formControlName=email]').type("yoga@studio.com")
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
  
      cy.url().should('include', '/sessions')
    })
  
    afterEach(() => {
      cy.window().then((window) => {
        window.sessionStorage.clear()
        window.localStorage.clear()
      })
    })
  
    describe('Sessions List', () => {
      it('should display all sessions', () => {
        cy.get('mat-card-header mat-card-title').should('contain', 'Rentals available')
        cy.get('.item').should('have.length.greaterThan', 0)
      })
  
      it('should navigate to create session when clicking create button', () => {
        cy.get('button').contains('Create').click()
        cy.url().should('include', '/sessions/create')
      })
    })
  
    describe('Session Details', () => {
      beforeEach(() => {      
        cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail')

        cy.get('mat-card-actions button').contains('Detail').click()
      })
  
      it('should display session details', () => {
        cy.get('mat-card-title div h1').should('be.visible')
        cy.get('.description').should('be.visible')
      })
  
      it('should allow admin to delete session', () => {
        cy.intercept('DELETE', '/api/session/1', {
          statusCode: 200
        }).as('deleteSession')
  
        cy.get('button span').contains('Delete').click()
        cy.url().should('equal', 'http://localhost:4200/sessions')
      })
    })
  
    describe('Session Form', () => {
      beforeEach(() => {
        cy.intercept('GET', '/api/auth/me', {
          body: {
            id: 1,
            username: 'userName',
            firstName: 'firstName',
            lastName: 'lastName',
            admin: true
          }
        }).as('me')
        cy.intercept('GET', '/api/teacher', { fixture: 'teachers.json' }).as('getTeachers')
      })
  
      it('should create new session', () => {
        cy.get('button').contains('Create').click()
        cy.intercept('POST', '/api/session', {
          statusCode: 201,
          fixture: 'new-session.json'
        }).as('createSession')
        
        cy.get('input[formControlName="name"]').type('New Yoga Session')
        cy.get('input[formControlName="date"]').type('2024-01-01')
        cy.get('mat-select[formControlName="teacher_id"]').click()
        cy.get('mat-option').first().click()
        cy.get('textarea[formControlName="description"]').type('Test description')
        
        cy.get('button[type="submit"]').click()
        cy.url().should('include', '/sessions')
      })
  
      it('should update existing session', () => {
        cy.intercept('GET', '/api/session/1', {
          statusCode: 200,
          fixture: 'session-detail.json'
        }).as('updateSession')  
        
        cy.get('button').contains('Edit').click()
        cy.get('input[formControlName="name"]').clear().type('Updated Session')
        cy.get('button[type="submit"]').click()
        cy.url().should('include', '/sessions')
      })
    })

  })
  
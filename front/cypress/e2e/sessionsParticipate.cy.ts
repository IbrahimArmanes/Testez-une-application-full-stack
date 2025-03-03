describe('Sessions Participation', () => {
    beforeEach(() => {
      cy.visit('/login')
  
      cy.intercept('POST', '/api/auth/login', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: false
        },
      })
  
      cy.intercept('GET', '/api/auth/me', {
        body: {
          id: 1,
          username: 'userName',
          firstName: 'firstName',
          lastName: 'lastName',
          admin: false
        }
      }).as('me')
      cy.intercept('GET', '/api/session', { fixture: 'sessions.json' }).as('sessions')
      
      cy.get('input[formControlName=email]').type("yoga@studio.com")
      cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)
  
      cy.url().should('include', '/sessions')
    })

    describe('Session Participation', () => {
      beforeEach(() => {
        cy.intercept('GET', '/api/auth/me', {
          body: {
            id: 1,
            username: 'userName',
            firstName: 'firstName',
            lastName: 'lastName',
            admin: false
          }
        }).as('me')
        
        cy.intercept('GET', '/api/teacher/1', { fixture: 'teachers.json' }).as('getTeachers')
        
      })
  
      it('should allow user to participate in session', () => {
        cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail')
        cy.get('button').contains('Detail').click()
        cy.intercept('POST', '/api/session/1/participate/*', {
          statusCode: 200
        }).as('participateSession')
        cy.intercept('GET', '/api/session/1', { fixture: 'session-detail-afterclick.json' }).as('getSessionDetail')
        cy.get('button').contains('Participate').click()
        cy.get('button').contains('Do not participate').should('be.visible')
      })
  
      it('should allow user to cancel participation', () => {
        cy.intercept('GET', '/api/session/1', { fixture: 'session-detail-afterclick.json' }).as('getSessionDetail')
        cy.get('button').contains('Detail').click()
        cy.intercept('DELETE', '/api/session/1/participate/*', {
          statusCode: 200
        }).as('unParticipateSession')
        
        cy.intercept('GET', '/api/session/1', { fixture: 'session-detail.json' }).as('getSessionDetail')
        cy.get('button').contains('Do not participate').click()
        cy.get('button').contains('Participate').should('be.visible')
      })
    })
})

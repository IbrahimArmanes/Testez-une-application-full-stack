// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
declare namespace Cypress {
  interface Chainable {
    login(): void;
    loginAsAdmin(): void;
  }
}

// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

Cypress.Commands.add('login', () => {
  const sessionInfo = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'testUser',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };
  
  window.localStorage.setItem('sessionInformation', JSON.stringify(sessionInfo));
  window.localStorage.setItem('isLogged', 'true');
});

Cypress.Commands.add('loginAsAdmin', () => {
  const adminSessionInfo = {
    token: 'fake-jwt-token',
    type: 'Bearer',
    id: 1,
    username: 'adminUser',
    firstName: 'Admin',
    lastName: 'User',
    admin: true
  };
  
  window.localStorage.setItem('sessionInformation', JSON.stringify(adminSessionInfo));
  window.localStorage.setItem('isLogged', 'true');
});
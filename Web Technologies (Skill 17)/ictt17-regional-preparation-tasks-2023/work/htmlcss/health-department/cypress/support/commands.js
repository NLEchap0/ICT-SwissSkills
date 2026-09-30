// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add("X", { prevSubject: "element" }, (subject) => {
  return subject[0].getBoundingClientRect().left;
});

Cypress.Commands.add("centerX", { prevSubject: "element" }, (subject) => {
  return (
    subject[0].getBoundingClientRect().left +
    subject[0].getBoundingClientRect().width / 2
  );
});

Cypress.Commands.add("endX", { prevSubject: "element" }, (subject) => {
  return subject[0].getBoundingClientRect().right;
});

Cypress.Commands.add("centerY", { prevSubject: "element" }, (subject) => {
  return (
    subject[0].getBoundingClientRect().top +
    subject[0].getBoundingClientRect().height / 2
  );
});

Cypress.Commands.add("Y", { prevSubject: "element" }, (subject) => {
  return subject[0].getBoundingClientRect().top;
});

Cypress.Commands.add("endY", { prevSubject: "element" }, (subject) => {
  return subject[0].getBoundingClientRect().bottom;
});

Cypress.Commands.add("width", { prevSubject: "element" }, (subject) => {
  return subject[0].getBoundingClientRect().width;
});

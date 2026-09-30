Cypress.Commands.overwrite('clock', (original, ...args) => {
    const clock = original(...args)
    clock.bind(window)
    return clock
})

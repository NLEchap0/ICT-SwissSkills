/**
 * STRING_COMPETITION STRING_YEAR - STRING_TRADE - STRING_TITLE
 * Frontend - Task 1
 *
 * Your task is to implement all methods marked with @todo.
 * You are allowed to add additional methods and variables to this file if you need to.
 * You are not allowed to change the signature of or delete existing methods.
 */

import Timer from './timer.js'

/**
 * @todo
 * 
 * Correctly add or remove the 'hidden' class
 * to the buttons to set their visibility
 * based on the timer's state.
 * 
 * If the timer is idle, only the start button should be visible.
 * If the timer is running, both the pause and the reset button should be visible.
 * If the timer is paused, both the continue and the reset button should be visible.
 * If the timer is finished, only the reset button should be visible.
 * 
 * @param {string} state - One of: 'idle', 'running', 'paused', 'finished'
 */
function toggleButtonsBasedOnState(state) {}

/**
 * @todo
 * 
 * Get the button elements from the DOM and
 * add event listeners to them that call
 * the corresponding methods on the timer.
 * Also call the toggleButtonsBasedOnState method
 * with the timer's state to correctly
 * show or hide the buttons.
 * 
 * @param {Timer} timer
 */
function initButtons(timer) {}

/**
 * !!! DO NOT CHANGE ANYTHING BELOW THIS LINE !!!
 */

function initTimer(time) {
    const clockElement = document.getElementById('clock')
    const timer = new Timer(clockElement, time, () => {
        toggleButtonsBasedOnState('finished')
    })

    initButtons(timer)
    toggleButtonsBasedOnState('idle')

    return timer
}

initTimer(10 * 60 * 1000)

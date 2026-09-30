/**
 * STRING_COMPETITION STRING_YEAR - STRING_TRADE - STRING_TITLE
 * Frontend - Task 1
 *
 * Your task is to implement all methods marked with @todo.
 * You are allowed to add additional methods and properties to the Timer class if you need to.
 * You are not allowed to change the signature of or delete existing methods.
 */

import Clock from './clock.js'
import Interval from './interval.js'

class Timer {
    /**
     * @todo
     * 
     * Initialize the timer.
     * Also create instances of Clock and Interval to use later on.
     * Do not start the timer just yet.
     * 
     * @param {HTMLElement} element - The HTML element of the clock to use for the timer.
     * @param {number} startMillis - The initial time in milliseconds.
     * @param {function} finishCallback - The callback to call when the timer finishes.
     */
    constructor(element, startMillis = 0, finishCallback = null) {}

    /**
     * @todo
     * 
     * Return the instance of Clock used.
     * 
     * @return {Clock}
     */
    getClock() {}

    /**
     * @todo
     * 
     * Start the timer if it is idle.
     * It should start counting down
     * from the initial time.
     */
    start() {}

    /**
     * @todo
     * 
     * Pause the timer if it is running.
     */
    pause() {}

    /**
     * @todo
     * 
     * Continue the timer if it is paused.
     */
    continue() {}

    /**
     * @todo
     * 
     * Pause the timer if it is running
     * and reset it to the initial state.
     */
    reset() {}
}

export default Timer

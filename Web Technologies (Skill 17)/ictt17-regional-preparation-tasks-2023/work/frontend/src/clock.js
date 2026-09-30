/**
 * STRING_COMPETITION STRING_YEAR - STRING_TRADE - STRING_TITLE
 * Frontend - Task 1
 *
 * Your task is to implement all methods marked with @todo.
 * You are allowed to add additional methods and properties to the Clock class if you need to.
 * You are not allowed to change the signature of or delete existing methods.
 */

import Digit from './digit.js'

class Clock {
    /**
     * @todo
     *
     * Initialize the clock with the given element
     * and the individual digits, so it can be used
     * to display the current time of the timer.
     * Also update the digits so they reflect
     * the initial time correctly.
     *
     * Throw an error if the given value is negative.
     *
     * @param {HTMLElement} element - The HTML element of the clock to use to display the time.
     * @param {number} millis - The current time of the timer in milliseconds.
     */
    constructor(element, millis = 0) {}

    /**
     * @todo
     *
     * Return the HTML element of the clock.
     *
     * @return {HTMLElement}
     */
    getElement() {}

    /**
     * @todo
     *
     * Return the current time of the timer in milliseconds.
     *
     * @return {number}
     */
    getMillis() {}

    /**
     * @todo
     *
     * Return the current time in seconds.
     * Always round up so the timer does not
     * display '500ms' as '0s'.
     *
     * @return {number}
     */
    getSeconds() {}

    /**
     * @todo
     *
     * Return the number of seconds to display.
     * Eg. If the current time is 65 seconds,
     * return 5, since the other 60 seconds
     * will be display be the minute-digits.
     *
     * @return {number}
     */
    getModuloSeconds() {}

    /**
     * @todo
     *
     * Return the number of minutes to display.
     * Always round down so the minutes can be
     * displayed by the minute-digits.
     *
     * @return {number}
     */
    getFlooredMinutes() {}

    /**
     * @todo
     *
     * Return the HTML element of a given digit.
     * Every digit-element has a specific id to identify it.
     * The id consists of the unit and the decimal concatenated by a dash.
     *
     * @param {string} unit - The unit of the requested digit. One of: 'minutes', 'seconds'.
     * @param {number} decimal - The decimal of the requested digit. One of: 1, 10.
     *
     * @return {HTMLElement}
     */
    getDigitElement(unit, decimal) {}

    /**
     * @todo
     *
     * Return the instance of a given digit.
     * Read {@link Clock#getDigitElement} for more information
     * on how a digit is identified.
     *
     * @param {string} unit - The unit of the requested digit. One of: 'minutes', 'seconds'.
     * @param {number} decimal - The decimal of the requested digit. One of: 1, 10.
     *
     * @return {Digit}
     */
    getDigit(unit, decimal) {}

    /**
     * @todo
     *
     * Set the current time of the timer in milliseconds.
     * Also update the digits so they reflect
     * the current time correctly.
     *
     * Throw an error if the given value is negative.
     *
     * @param {number} millis - The new time in milliseconds.
     */
    setMillis(millis) {}

    /**
     * @todo
     *
     * Add the given number of milliseconds to the current time.
     * Also update the digits so they reflect the current time correctly.
     * Note: The same method is used to subtract time by passing a negative number.
     *
     * @param {number} millis - The number of milliseconds to add to the current time.
     */
    addMillis(millis) {}
}

export default Clock

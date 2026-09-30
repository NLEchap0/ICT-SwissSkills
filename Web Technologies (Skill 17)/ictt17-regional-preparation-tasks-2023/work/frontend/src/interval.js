/**
 * STRING_COMPETITION STRING_YEAR - STRING_TRADE - STRING_TITLE
 * Frontend - Task 1
 *
 * Your task is to implement all methods marked with @todo.
 * You are allowed to add additional methods and properties to the Interval class if you need to.
 * You are not allowed to change the signature of or delete existing methods.
 */

class Interval {
    /**
     * @todo
     * 
     * Initialize the interval so it can be started later on
     * without having to pass any parameters.
     * 
     * @param {function} callback - The callback to call every {@link Interval#millis} milliseconds.
     * @param {number} millis - The time in milliseconds between each call of the callback.
     */
    constructor(callback, millis) {}

    /**
     * @todo
     * 
     * Start the interval if it has not been started yet.
     * It should start calling the callback given initially
     * every {@link Interval#millis} milliseconds.
     * As a parameter of the callback, pass the time in milliseconds
     * that has passed since the last call of the callback.
     */
    start() {}

    /**
     * @todo
     * 
     * Pause the interval if it is running.
     */
    pause() {}

    /**
     * @todo
     * 
     * Continue the interval if it is paused.
     * The next call of the callback should happen
     * after the time that was left until
     * the next call before it had been paused.
     */
    continue() {}
}

export default Interval

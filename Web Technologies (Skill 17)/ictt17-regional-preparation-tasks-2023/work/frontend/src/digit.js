/**
 * STRING_COMPETITION STRING_YEAR - STRING_TRADE - STRING_TITLE
 * Frontend - Task 1
 *
 * Your task is to implement all methods marked with @todo.
 * You are allowed to add additional methods and properties to the Digit class if you need to.
 * You are not allowed to change the signature of or delete existing methods.
 */

class Digit {
    /**
     * @todo
     * 
     * Initialize the digit with the given element
     * so it can be used to display a given value.
     * Also update every stroke's element within
     * the digit's element to reflects
     * the initial value correctly.
     * 
     * Throw an error if the given value is not between 0 and 9.
     * 
     * @param {HTMLElement} element - The HTML element of the digit to use to display the value.
     * @param {number} value - The initial value of the digit.
     */
    constructor(element, value = 8) {}

    /**
     * @todo
     * 
     * Return the HTML element of the digit.
     * 
     * @return {HTMLElement}
     */
    getElement() {}

    /**
     * @todo
     * 
     * Return the current value of the digit.
     * 
     * @return {number}
     */
    getValue() {}

    /**
     * @todo
     * 
     * Set the value of the digit.
     * Also update every stroke's element within
     * the digit's element to reflect the new value correctly.
     * To do so, you may add or remove the 'on' class from the stroke's element.
     * 
     * Throw an error if the given value is not between 0 and 9.
     * 
     * @param {number} value - The new value of the digit.
     */
    setValue(value) {}
}

export default Digit

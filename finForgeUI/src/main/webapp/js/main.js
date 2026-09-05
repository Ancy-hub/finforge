/**
 * Smart FinForge — Main JavaScript
 * Pure vanilla JS (no jQuery, no frameworks)
 */

'use strict';

// ================================================================
// Password visibility toggle
// ================================================================

/**
 * Toggles the visibility of a password input field.
 * @param {string} inputId - The ID of the password input element.
 */
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return;

    if (input.type === 'password') {
        input.type = 'text';
    } else {
        input.type = 'password';
    }
}

// ================================================================
// Auto-dismiss alerts after 5 seconds
// ================================================================

(function autoDismissAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity .5s';
            alert.style.opacity    = '0';
            setTimeout(function () { alert.remove(); }, 500);
        }, 5000);
    });
})();

// ================================================================
// Set today's date as default for date inputs if empty
// ================================================================

(function setDefaultDates() {
    const today = new Date().toISOString().split('T')[0];
    const dateInputs = document.querySelectorAll('input[type="date"]:not([value])');
    dateInputs.forEach(function (input) {
        if (!input.value) {
            input.value = today;
        }
    });
})();

// ================================================================
// Form validation — highlight empty required fields on submit
// ================================================================

(function addClientValidation() {
    const forms = document.querySelectorAll('form[novalidate]');
    forms.forEach(function (form) {
        form.addEventListener('submit', function (e) {
            let valid = true;

            form.querySelectorAll('[required]').forEach(function (field) {
                field.classList.remove('field-error');
                if (!field.value.trim()) {
                    field.classList.add('field-error');
                    valid = false;
                }
            });

            // Check passwords match if confirm field exists
            const pw  = form.querySelector('#password, #newPassword');
            const cpw = form.querySelector('#confirmPassword');
            if (pw && cpw && pw.value && cpw.value && pw.value !== cpw.value) {
                cpw.classList.add('field-error');
                showInlineError(cpw, 'Passwords do not match.');
                valid = false;
            }

            // Validate amount fields > 0
            form.querySelectorAll('input[type="number"]').forEach(function (numField) {
                if (numField.value && parseFloat(numField.value) <= 0) {
                    numField.classList.add('field-error');
                    valid = false;
                }
            });

            if (!valid) {
                e.preventDefault();
                const firstError = form.querySelector('.field-error');
                if (firstError) {
                    firstError.focus();
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });
    });

    // Clear error highlight on input
    document.querySelectorAll('.form-control').forEach(function (ctrl) {
        ctrl.addEventListener('input', function () {
            ctrl.classList.remove('field-error');
            const msg = ctrl.parentElement.querySelector('.field-error-msg');
            if (msg) msg.remove();
        });
    });
})();

/**
 * Appends an inline error message below the given field.
 * @param {HTMLElement} field
 * @param {string}      message
 */
function showInlineError(field, message) {
    let msg = field.parentElement.querySelector('.field-error-msg');
    if (!msg) {
        msg = document.createElement('span');
        msg.className = 'field-error-msg';
        field.parentElement.appendChild(msg);
    }
    msg.textContent = message;
}

// Inject inline error style dynamically (keeps HTML clean)
(function injectFieldErrorStyle() {
    const style = document.createElement('style');
    style.textContent = `
        .form-control.field-error {
            border-color: #dc2626 !important;
            box-shadow: 0 0 0 3px #fee2e2 !important;
        }
        .field-error-msg {
            display: block;
            font-size: .78rem;
            color: #dc2626;
            margin-top: .25rem;
        }
    `;
    document.head.appendChild(style);
})();

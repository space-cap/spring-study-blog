// TxPlan Admin JavaScript

$(document).ready(function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 5000);
    
    // Tooth chart interactions
    initializeToothChart();
    
    // Form validations
    initializeFormValidations();
    
    // Search functionality
    initializeSearch();
});

// Tooth Chart Functions
function initializeToothChart() {
    $('.tooth-item').on('click', function() {
        const toothNumber = $(this).data('tooth');
        toggleToothStatus(toothNumber);
    });
    
    $('.tooth-item').on('contextmenu', function(e) {
        e.preventDefault();
        const toothNumber = $(this).data('tooth');
        showToothContextMenu(toothNumber, e.pageX, e.pageY);
    });
}

function toggleToothStatus(toothNumber) {
    const toothElement = $(`.tooth-item[data-tooth="${toothNumber}"] .tooth-visual`);
    const currentStatus = toothElement.attr('class').split(' ').find(cls => 
        ['decayed', 'filled', 'missing', 'crown', 'implant'].includes(cls)
    );
    
    // Cycle through statuses
    const statuses = ['normal', 'decayed', 'filled', 'missing', 'crown', 'implant'];
    const currentIndex = statuses.indexOf(currentStatus || 'normal');
    const nextIndex = (currentIndex + 1) % statuses.length;
    const nextStatus = statuses[nextIndex];
    
    // Remove all status classes
    toothElement.removeClass('decayed filled missing crown implant');
    
    // Add new status class (except for normal)
    if (nextStatus !== 'normal') {
        toothElement.addClass(nextStatus);
    }
    
    // Update hidden form field if exists
    const hiddenField = $(`input[name="tooth_${toothNumber}_status"]`);
    if (hiddenField.length) {
        hiddenField.val(nextStatus);
    }
    
    console.log(`Tooth ${toothNumber} status changed to: ${nextStatus}`);
}

function showToothContextMenu(toothNumber, x, y) {
    // Remove existing context menu
    $('.tooth-context-menu').remove();
    
    const menu = $(`
        <div class="tooth-context-menu" style="position: absolute; left: ${x}px; top: ${y}px; z-index: 1000;">
            <div class="card">
                <div class="card-body p-2">
                    <h6 class="card-title mb-2">치아 ${toothNumber}</h6>
                    <div class="btn-group-vertical w-100" role="group">
                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="setToothStatus('${toothNumber}', 'normal')">정상</button>
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="setToothStatus('${toothNumber}', 'decayed')">우식</button>
                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="setToothStatus('${toothNumber}', 'filled')">충전</button>
                        <button type="button" class="btn btn-sm btn-outline-warning" onclick="setToothStatus('${toothNumber}', 'missing')">결손</button>
                        <button type="button" class="btn btn-sm btn-outline-info" onclick="setToothStatus('${toothNumber}', 'crown')">크라운</button>
                        <button type="button" class="btn btn-sm btn-outline-success" onclick="setToothStatus('${toothNumber}', 'implant')">임플란트</button>
                    </div>
                </div>
            </div>
        </div>
    `);
    
    $('body').append(menu);
    
    // Close menu when clicking outside
    $(document).one('click', function() {
        $('.tooth-context-menu').remove();
    });
}

function setToothStatus(toothNumber, status) {
    const toothElement = $(`.tooth-item[data-tooth="${toothNumber}"] .tooth-visual`);
    
    // Remove all status classes
    toothElement.removeClass('decayed filled missing crown implant');
    
    // Add new status class (except for normal)
    if (status !== 'normal') {
        toothElement.addClass(status);
    }
    
    // Update hidden form field if exists
    const hiddenField = $(`input[name="tooth_${toothNumber}_status"]`);
    if (hiddenField.length) {
        hiddenField.val(status);
    }
    
    // Close context menu
    $('.tooth-context-menu').remove();
    
    console.log(`Tooth ${toothNumber} status set to: ${status}`);
}

// Form Validation Functions
function initializeFormValidations() {
    // Bootstrap form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Custom validations
    $('#birthDate').on('change', function() {
        const birthDate = new Date($(this).val());
        const today = new Date();
        const age = today.getFullYear() - birthDate.getFullYear();
        
        if (age < 0 || age > 150) {
            $(this).addClass('is-invalid');
            $(this).siblings('.invalid-feedback').text('올바른 생년월일을 입력해주세요.');
        } else {
            $(this).removeClass('is-invalid');
        }
    });
}

// Search Functions
function initializeSearch() {
    // Real-time search
    let searchTimeout;
    $('input[name="search"]').on('input', function() {
        clearTimeout(searchTimeout);
        const searchTerm = $(this).val();
        
        if (searchTerm.length >= 2) {
            searchTimeout = setTimeout(function() {
                performSearch(searchTerm);
            }, 500);
        }
    });
}

function performSearch(searchTerm) {
    // This would typically make an AJAX call to search
    console.log('Searching for:', searchTerm);
    
    // For now, just highlight matching text in the table
    $('.table tbody tr').each(function() {
        const rowText = $(this).text().toLowerCase();
        const isMatch = rowText.includes(searchTerm.toLowerCase());
        
        if (isMatch) {
            $(this).show().addClass('table-warning');
        } else {
            $(this).removeClass('table-warning');
            if (searchTerm.length > 0) {
                $(this).hide();
            } else {
                $(this).show();
            }
        }
    });
}

// PDF Functions
function downloadPdf(planId, type) {
    const url = `/treatment-plans/${planId}/pdf/${type}`;
    
    // Show loading indicator
    const button = event.target.closest('button');
    const originalText = button.innerHTML;
    button.innerHTML = '<span class="loading"></span> 생성 중...';
    button.disabled = true;
    
    // Create a temporary link to download the PDF
    const link = document.createElement('a');
    link.href = url;
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    // Restore button after a delay
    setTimeout(function() {
        button.innerHTML = originalText;
        button.disabled = false;
    }, 2000);
}

// Treatment Plan Functions
function calculateTotalCost(planId) {
    $.ajax({
        url: `/treatment-plans/${planId}/calculate`,
        method: 'POST',
        success: function(data) {
            // Update the displayed costs
            $('.total-cost').text(formatCurrency(data.totalCost));
            $('.insurance-cost').text(formatCurrency(data.insuranceCost));
            $('.self-cost').text(formatCurrency(data.selfCost));
            
            showAlert('진료비가 계산되었습니다.', 'success');
        },
        error: function() {
            showAlert('진료비 계산 중 오류가 발생했습니다.', 'danger');
        }
    });
}

// Utility Functions
function formatCurrency(amount) {
    if (!amount) return '0원';
    return new Intl.NumberFormat('ko-KR').format(amount) + '원';
}

function showAlert(message, type = 'info') {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('.alert-container, main').first().prepend(alertHtml);
    
    // Auto-hide after 5 seconds
    setTimeout(function() {
        $('.alert').first().fadeOut('slow', function() {
            $(this).remove();
        });
    }, 5000);
}

function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Chart Number Generation
function generateChartNumber() {
    // This would typically make an AJAX call to get the next chart number
    const timestamp = Date.now();
    const randomNum = Math.floor(Math.random() * 1000);
    return `${timestamp}${randomNum}`.slice(-6);
}

// Export functions for global access
window.TxPlanAdmin = {
    toggleToothStatus,
    setToothStatus,
    downloadPdf,
    calculateTotalCost,
    showAlert,
    confirmAction,
    generateChartNumber
};


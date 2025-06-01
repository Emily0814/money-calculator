function appendNumber(num) {
	
    currentInput += num;
    display.value = currentInput;
	
}

function appendOperator(op) {
	
    if (currentInput === '') return;
    
    if (previousInput !== '' && operator !== '') {
        calculate();
    }
    
    operator = op;
    console.log('저장된 operator:', operator);
    previousInput = currentInput;
    currentInput = '';
    display.value = previousInput + ' ' + (op === '*' ? '×' : op) + ' ';
	
}

function calculate() {
	
    if (previousInput === '' || currentInput === '' || operator === '') return;
    
    const params = new URLSearchParams();
    params.append('num1', previousInput);
    params.append('num2', currentInput);
    params.append('operator', operator);

    fetch('/api/calculate', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
    .then(response => response.text())
    .then(result => {
        display.value = result;
        currentInput = result;
        previousInput = '';
        operator = '';
    })
    .catch(error => {
        alert('계산 오류가 발생했습니다.');
        console.error(error);
    });
	
}

function clearDisplay() {
	
    currentInput = '';
    previousInput = '';
    operator = '';
    display.value = '';
	
}

function deleteLast() {
	
    currentInput = currentInput.slice(0, -1);
    display.value = currentInput;
	
}
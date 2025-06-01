function calculateLoan() {
	
    const principal = document.getElementById('loanAmount').value;
    const rate = document.getElementById('interestRate').value;
    const months = document.getElementById('loanPeriod').value;
    
    if (!principal || !rate || !months) {
        alert('모든 값을 입력해주세요.');
        return;
    }
    
    fetch('/api/loan', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `principal=${principal}&rate=${rate}&months=${months}`
    })
    .then(response => response.text())
    .then(result => {
        const monthlyPayment = parseFloat(result);
        const totalPayment = monthlyPayment * parseInt(months);
        const totalInterest = totalPayment - parseFloat(principal);
        
        document.getElementById('resultPrincipal').textContent = parseInt(principal).toLocaleString();
        document.getElementById('resultMonthly').textContent = Math.round(monthlyPayment).toLocaleString();
        document.getElementById('resultTotal').textContent = Math.round(totalPayment).toLocaleString();
        document.getElementById('resultInterest').textContent = Math.round(totalInterest).toLocaleString();
        document.getElementById('result').style.display = 'block';
    })
    .catch(error => {
        alert('계산 오류가 발생했습니다.');
        console.error(error);
    });
	
}
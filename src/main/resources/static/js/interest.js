function calculateInterest() {
	
    const principal = document.getElementById('principal').value;
    const rate = document.getElementById('rate').value;
    const years = document.getElementById('years').value;
    
    if (!principal || !rate || !years) {
        alert('모든 값을 입력해주세요.');
        return;
    }
    
    fetch('/api/interest', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `principal=${principal}&rate=${rate}&years=${years}&type=compound`
    })
    .then(response => response.text())
    .then(result => {
        const finalAmount = parseFloat(result);
        const interest = finalAmount - parseFloat(principal);
        
        document.getElementById('resultPrincipal').textContent = parseInt(principal).toLocaleString();
        document.getElementById('resultAmount').textContent = Math.round(finalAmount).toLocaleString();
        document.getElementById('resultInterest').textContent = Math.round(interest).toLocaleString();
        document.getElementById('result').style.display = 'block';
    })
    .catch(error => {
        alert('계산 오류가 발생했습니다.');
        console.error(error);
    });
	
}
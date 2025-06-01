let currentRate = 0;

function getCurrentRate() {
	
    const fromCurrency = document.getElementById('fromCurrency').value;
    const toCurrency = document.getElementById('toCurrency').value;
    
 	//디버깅용 로그 추가
    console.log('fromCurrency:', fromCurrency);
    console.log('toCurrency:', toCurrency);
    
    const url = `/api/exchange-rate?from=${fromCurrency}&to=${toCurrency}`;
    console.log('요청 URL:', url);  //URL 확인
    
    fetch(url)
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('환율 조회에 실패했습니다.');
        }
        return response.text();
    })
    
    if (fromCurrency === toCurrency) {
        alert('기준 통화와 환전 통화가 같습니다.');
        return;
    }
    
    //로딩 표시
    const rateResult = document.getElementById('rateResult');
    const rateInfo = document.getElementById('rateInfo');
    rateInfo.textContent = '환율 조회 중...';
    rateResult.style.display = 'block';
    
    fetch(`/api/exchange-rate?from=${fromCurrency}&to=${toCurrency}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('환율 조회에 실패했습니다.');
            }
            return response.text();
        })
        .then(rate => {
            currentRate = parseFloat(rate);
            rateInfo.textContent = `1 ${fromCurrency} = ${currentRate.toLocaleString()} ${toCurrency}`;
        })
        .catch(error => {
            rateInfo.textContent = '환율 조회에 실패했습니다. 다시 시도해주세요.';
            console.error(error);
        });
		
}

function convertCurrency() {
	
    const amount = document.getElementById('amount').value;
    const fromCurrency = document.getElementById('fromCurrency').value;
    const toCurrency = document.getElementById('toCurrency').value;
    
    if (!amount) {
        alert('환전할 금액을 입력해주세요.');
        return;
    }
    
    if (fromCurrency === toCurrency) {
        alert('기준 통화와 환전 통화가 같습니다.');
        return;
    }
    
    fetch('/api/exchange', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `amount=${amount}&from=${fromCurrency}&to=${toCurrency}`
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('환전 계산에 실패했습니다.');
        }
        return response.text();
    })
    .then(result => {
        const convertedAmount = parseFloat(result);
        
        //환율 다시 조회해서 표시
        fetch(`/api/exchange-rate?from=${fromCurrency}&to=${toCurrency}`)
            .then(response => response.text())
            .then(rate => {
                document.getElementById('originalAmount').textContent = 
                    `${parseFloat(amount).toLocaleString()} ${fromCurrency}`;
                document.getElementById('appliedRate').textContent = 
                    `1 ${fromCurrency} = ${parseFloat(rate).toLocaleString()} ${toCurrency}`;
                document.getElementById('convertedAmount').textContent = 
                    `${convertedAmount.toLocaleString()} ${toCurrency}`;
                document.getElementById('convertResult').style.display = 'block';
            });
    })
    .catch(error => {
        alert('환전 계산 중 오류가 발생했습니다.');
        console.error(error);
    });
	
}

//페이지 로드 시 기본 환율 조회
window.onload = function() {
	
    getCurrentRate();
	
};
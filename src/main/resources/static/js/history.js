//페이지 로드 시 전체 기록 불러오기
window.onload = function() {
	
	//첫 번째 버튼 활성화
    document.querySelector('.btn-group .btn').classList.add('active');
    
    //데이터 로딩 (이벤트 X)
    let url = '/api/history';
    document.getElementById('tableTitle').textContent = '전체 계산 기록';
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            displayHistory(data);
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('historyTableBody').innerHTML = 
                '<tr><td colspan="5" class="text-center text-danger">데이터 로딩 실패</td></tr>';
        });
    
    loadStatistics();
	
};

//계산 기록 불러오기
function loadHistory(type) {
	
    let url = '/api/history';
    let title = '전체 계산 기록';
    
    if (type !== 'all') {
        url = `/api/history/${type}`;
        title = getTypeTitle(type) + ' 기록';
    }
    
    //버튼 활성화 상태 변경
    document.querySelectorAll('.btn-group .btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    document.getElementById('tableTitle').textContent = title;
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            displayHistory(data);
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('historyTableBody').innerHTML = 
                '<tr><td colspan="5" class="text-center text-danger">데이터 로딩 실패</td></tr>';
        })

}

//기록 표시
function displayHistory(histories) {
	
    const tbody = document.getElementById('historyTableBody');
    
    if (histories.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">기록이 없습니다.</td></tr>';
        return;
    }
    
    tbody.innerHTML = histories.map(history => `
        <tr>
            <td>${history.id}</td>
            <td><span class="badge ${getTypeBadgeClass(history.calcType)}">${getTypeTitle(history.calcType)}</span></td>
            <td><small>${formatInputData(history.inputData)}</small></td>
            <td><strong>${history.result}</strong></td>
            <td><small>${formatDateTime(history.createdAt)}</small></td>
        </tr>
    `).join('');
	
}

//통계 정보 로드
function loadStatistics() {
	
    fetch('/api/history')
        .then(response => response.json())
        .then(data => {
            const stats = {
                total: data.length,
                basic: data.filter(h => h.calcType === 'basic').length,
                interest: data.filter(h => h.calcType === 'interest').length,
                loan: data.filter(h => h.calcType === 'loan').length,
                exchange: data.filter(h => h.calcType === 'exchange').length
            };
            
            document.getElementById('totalCount').textContent = stats.total;
            document.getElementById('basicCount').textContent = stats.basic;
            document.getElementById('interestCount').textContent = stats.interest;
            document.getElementById('loanCount').textContent = stats.loan;
            document.getElementById('exchangeCount').textContent = stats.exchange;
        });
		
}

//계산 타입별 제목
function getTypeTitle(type) {
	
    const titles = {
        'basic': '기본 계산',
        'interest': '이자 계산',
        'loan': '대출 계산',
        'exchange': '환율 계산'
    };
    return titles[type] || type;
	
}

//계산 타입별 배지 클래스
function getTypeBadgeClass(type) {
	
    const classes = {
        'basic': 'bg-primary',
        'interest': 'bg-success',
        'loan': 'bg-warning text-dark',
        'exchange': 'bg-info'
    };
    return classes[type] || 'bg-secondary';
	
}

//입력 데이터 포맷팅
function formatInputData(inputData) {
	
    try {
        const data = JSON.parse(inputData);
        
        if (data.num1 !== undefined) {
            return `${data.num1} ${data.operator} ${data.num2}`;
        } else if (data.principal !== undefined) {
            return `원금: ${Number(data.principal).toLocaleString()}원, 이율: ${data.rate}%, 기간: ${data.years || data.months}${data.years ? '년' : '개월'}`;
        } else if (data.amount !== undefined) {
            return `${Number(data.amount).toLocaleString()} ${data.from} → ${data.to}`;
        }
        
        return inputData;
    } catch (e) {
        return inputData;
    }
	
}

//날짜 시간 포맷팅
function formatDateTime(dateTime) {
	
    const date = new Date(dateTime);
    return date.getFullYear() + '-' + 
           String(date.getMonth() + 1).padStart(2, '0') + '-' + 
           String(date.getDate()).padStart(2, '0') + ' ' +
           String(date.getHours()).padStart(2, '0') + ':' + 
           String(date.getMinutes()).padStart(2, '0');
		   
}
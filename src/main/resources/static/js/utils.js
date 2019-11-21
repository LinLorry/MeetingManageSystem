const meetingFields = ['location', 'star', 'hotel', 'comment'];

function getProfile() {
    let url = '/api/user/profile';
    let headers = {
        'Authorization': 'Meeting ' + localStorage.token
    };

    fetch(url, {
        method: 'GET',
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            window.localStorage.profile = JSON.stringify(json.data);
        }
    })
}

function getMenus() {
    let url = '/api/user/menus';

    fetch(url, {
        headers: {
            'Authorization': 'Meeting ' + window.localStorage.token
        },
        method: 'GET'
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            window.localStorage.menus = JSON.stringify(json.data);
            loadMenus();
        }
    });
}

function loadMenus() {
    if (localStorage.menus === undefined){
        return ;
    }

    let select_ul = document.getElementById('select-ul');
    let menus = this.JSON.parse(localStorage.menus);

    if (menus.length != 0) {
        select_ul.innerHTML = '';
        menus.forEach(element => {
            let li = this.document.createElement('li');
            let a = this.document.createElement('a');
            li.appendChild(a);
    
            a.innerHTML = element.name;
            a.href = element.url;
    
            select_ul.appendChild(li);
        });

        let li = this.document.createElement('li');
        let a = this.document.createElement('a');
        li.appendChild(a);

        a.innerHTML = '登出';
        a.href = 'javascript:logout()';
        select_ul.appendChild(li);
    }
}

function judgeLogin() {
    let token = localStorage.token;
    let userProfileURL = '/api/user/profile';

    if (token != ''  && token != null) {
        fetch(userProfileURL, {
            headers: {
                'Authorization': 'Meeting ' + token
            },
            method: 'GET'
        })
        .then(function(response) {
            if(response.ok) {
                window.location.href = '/index.html';
            }
        })
    } else {
        window.location.href = '/index.html';
    }
}

function getUserId() {
    let profile = this.JSON.parse(window.localStorage.profile);
    return profile.id;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('menus');
    localStorage.removeItem('profile');
    location.href='/index.html';
}

function createDateBox() {
    let div = document.createElement('div');
    div.style.display = 'inline';    
    let yearLabel = document.createElement('label');
    let monthLabel = document.createElement('label');
    let dayLabel = document.createElement('label');

    let yearSelect = document.createElement('select');
    let monthSelect = document.createElement('select');
    let daySelect = document.createElement('select');

    div.appendChild(yearLabel);
    div.appendChild(monthLabel);
    div.appendChild(dayLabel);

    for (var i = 2000; i < 2050; i++)
    {
        let option = document.createElement('option');
        option.value = i;
        option.text = i;
        yearSelect.appendChild(option);
    } 

    for (var i = 0; i < 12; i++)
    {
        let option = document.createElement('option');
        option.value = i;
        option.text = i + 1;
        monthSelect.appendChild(option);
    }

    for (var i = 1; i < 32; i++)
    {
        let option = document.createElement('option');
        option.value = i;
        option.text = i;
        daySelect.appendChild(option);
    }

    yearLabel.appendChild(yearSelect);
    yearLabel.innerHTML = yearLabel.innerHTML + ' 年';
    monthLabel.appendChild(monthSelect);
    monthLabel.innerHTML = monthLabel.innerHTML + ' 月';
    dayLabel.appendChild(daySelect);
    dayLabel.innerHTML = dayLabel.innerHTML + ' 日';

    return div;
}

function createTimeBox() {
    let div = createDateBox();
    let hourLabel = document.createElement('label');
    let minuteLabel = document.createElement('label');

    let hourSelect = document.createElement('select');
    let minuteSelect = document.createElement('select');

    div.appendChild(hourLabel);
    div.appendChild(minuteLabel);

    for (var i = 0; i < 25; i++)
    {
        let option = document.createElement('option');
        option.value = i;
        option.text = i;
        hourSelect.appendChild(option);
    }

    for (var i = 0; i < 61; i++)
    {
        let option = document.createElement('option');
        option.value = i;
        option.text = i;
        minuteSelect.appendChild(option);
    }

    hourLabel.appendChild(hourSelect);
    hourLabel.innerHTML = hourLabel.innerHTML + ' 点';
    minuteLabel.appendChild(minuteSelect);
    minuteLabel.innerHTML = minuteLabel.innerHTML + ' 分';

    return div;
}

function stringifyDate(date) {
    let yearSelect = date.childNodes[0].childNodes[0];
    let monthSelect = date.childNodes[1].childNodes[0];
    let daySelect = date.childNodes[2].childNodes[0];

    let year = parseInt(yearSelect.options[yearSelect.selectedIndex].value);
    let month = parseInt(monthSelect.options[monthSelect.selectedIndex].value);
    let day = parseInt(daySelect.options[daySelect.selectedIndex].value);

    let result = new Date(year, month, day, 8).toISOString();

    result = result.replace('T', ' ');
    result = result.replace('Z', ' ');
    
    return result.split('.')[0];
}

function stringifyTime(date) {
    let yearSelect = date.childNodes[0].childNodes[0];
    let monthSelect = date.childNodes[1].childNodes[0];
    let daySelect = date.childNodes[2].childNodes[0];
    let hourSelect = date.childNodes[3].childNodes[0];
    let minuteSelect = date.childNodes[4].childNodes[0];

    let year = parseInt(yearSelect.options[yearSelect.selectedIndex].value);
    let month = parseInt(monthSelect.options[monthSelect.selectedIndex].value);
    let day = parseInt(daySelect.options[daySelect.selectedIndex].value);
    let hour = parseInt(hourSelect.options[hourSelect.selectedIndex].value);
    let minute = parseInt(minuteSelect.options[minuteSelect.selectedIndex].value);

    let result = new Date(year, month, day, hour + 8, minute).toISOString();

    result = result.replace('T', ' ');
    result = result.replace('Z', ' ');

    return result.split('.')[0];
}

function parseDate(time) {
    let re = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})/;
    let result = re.exec(time);

    return result[1] + '年' 
        + result[2] + '月' 
        + result[3] + '日' 
        + result[4] + '点' 
        + result[5] + '分';
}

function disposeHint(message) {
    let hint = document.getElementById('hint');
    hint.textContent = message;
    hint.style.display = 'block';
}
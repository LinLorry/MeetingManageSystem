getMenus();

window.onload = function() {
    this.loadMenus();

    let url = '/api/user/join';
    let tbody = this.document.getElementById('meetings-tbody')

    fetch(url, { method: 'GET' , headers: { 'Authorization': 'Meeting ' + this.localStorage.token}})
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            json.data.forEach(elem => {
                let tr = document.createElement('tr');
                tbody.appendChild(tr);
                
                let name = document.createElement('td');
                let a = document.createElement('a');
                a.href = '/meeting.html?id=' + elem.id; 
                a.innerHTML = elem.name;
                name.appendChild(a);
                tr.appendChild(name);

                let time = document.createElement('td');
                time.innerHTML = parseDate(elem.time);
                tr.appendChild(time);

                meetingFields.forEach(key => {
                    let td = document.createElement('td');
                    td.innerHTML = elem[key];
                    tr.appendChild(td);
                });

                let action = document.createElement('td');
                let unJoinButton = document.createElement('button');
                let checkInButton = document.createElement('button');

                tr.appendChild(action);
                action.appendChild(unJoinButton);
                action.appendChild(checkInButton);

                unJoinButton.onclick = function() { unJoin(elem.id) };
                unJoinButton.textContent = '不参加';

                // TODO when have check unappend this.
                checkInButton.id = 'check-in-' + elem.id + '-button';
                checkInButton.onclick = function() { checkIn(elem.id) };
                checkInButton.textContent = '与会打卡';

                tr.id = 'meeting-' + elem.id + '-tr';
            });
        }
    })
}

function unJoin(id) {
    let url = '/api/meeting/cancelJoin?id=' + id;

    fetch(url, {
        method: 'POST', 
        headers: { 
            'Authorization': 'Meeting ' + this.localStorage.token
        }})
    .then(response => response.json())
    .then(function(json) {
        let hint = document.getElementById('hint');
        hint.style.display = 'block'; 
        hint.innerHTML = json.message;
        if (json.status == 1) {
            let tr = document.getElementById('meeting-' + id + '-tr')
            if (tr !== null) {
                document.getElementById('meetings-tbody').removeChild(tr);
            }
        }
    });
}

function checkIn(id) {
    let url = '/api/meeting/checkIn?id=' + id;

    fetch(url, {
        method: 'POST', 
        headers: { 
            'Authorization': 'Meeting ' + this.localStorage.token
        }})
    .then(response => response.json())
    .then(function(json) {
        let hint = document.getElementById('hint');
        hint.style.display = 'block'; 
        hint.innerHTML = json.message;
        if (json.status == 1) {
            let button = document.getElementById('check-in-' + id + '-button')
            if (button !== null) {
                button.style.display = 'none';
            }
        }
    });
}
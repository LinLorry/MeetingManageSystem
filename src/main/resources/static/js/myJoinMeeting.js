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
                tr.appendChild(action);

                if (!elem.checkIn) {
                    let unJoinButton = document.createElement('button');
                    action.appendChild(unJoinButton);
                    unJoinButton.id = 'un-join-' + elem.id + '-button';
                    unJoinButton.onclick = function() { unJoin(elem.id) };
                    unJoinButton.textContent = '不参加';

                    let checkInButton = document.createElement('button');
                    action.appendChild(checkInButton);
                    checkInButton.id = 'check-in-' + elem.id + '-button';
                    checkInButton.onclick = function() { checkIn(elem.id) };
                    checkInButton.textContent = '与会打卡';
                }

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
        disposeHint(json.message);
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
        disposeHint(json.message);
        if (json.status == 1) {
            let unJoinButton = document.getElementById('un-join-' + id + '-button');
            let checkInButton = document.getElementById('check-in-' + id + '-button');
            if (unJoinButton !== null) {
                unJoinButton.style.display = 'none';
            }

            if (checkInButton !== null) {
                checkInButton.style.display = 'none';
            }
        }
    });
}
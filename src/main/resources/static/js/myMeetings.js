getMenus();

window.onload = function() {
    this.loadMenus();

    let url = "/api/user/meetings";
    let tbody = this.document.getElementById("meetings-tbody")

    fetch(url, { method: "GET" , headers: { "Authorization": "Meeting " + this.localStorage.token}})
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            json.data.forEach(elem => {
                let tr = document.createElement("tr");
                tbody.appendChild(tr);
                
                let name = document.createElement("td");
                let a = document.createElement("a");
                a.href = "/meeting.html?id=" + elem.id; 
                a.innerHTML = elem.name;
                name.appendChild(a);
                tr.appendChild(name);

                let time = document.createElement("td");
                time.innerHTML = parseDate(elem.time);
                tr.appendChild(time);

                meetingFields.forEach(key => {
                    let td = document.createElement("td");
                    td.innerHTML = elem[key];
                    tr.appendChild(td);
                });

                let action = document.createElement("td");
                let deleteButton = document.createElement("button");
                tr.appendChild(action);
                action.appendChild(deleteButton);

                deleteButton.onclick = function() { del(elem.id) };
                deleteButton.innerHTML = "删除";

                tr.id = 'meeting-' + elem.id + '-tr';
            });
        }
    })
}

function del(id) {
    let url = "/api/meeting/delete?id=";
    fetch(url, { 
        body: JSON.stringify({
            id: id
        }),
        method: "POST", 
        headers: { 
            "Authorization": "Meeting " + this.localStorage.token,
            'content-type': 'application/json'
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
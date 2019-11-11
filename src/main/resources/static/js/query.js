getMenus();

window.onload = loadMenus;

function query() {
    let regex = /^\d{4}-\d{2}-\d{2}$/
    let url = new URL("/api/meeting/get", "http://" + document.domain);

    let id = document.getElementById("id").value;
    let name = document.getElementById("name").value;
    let after = document.getElementById("after").value;
    let before = document.getElementById("before").value;
    let location = document.getElementById("location").value;

    let token = window.localStorage.token;
    let headers;

    if (id != "") url.searchParams.append("id", id);
    if (name != "") url.searchParams.append("name", name);
    if (regex.test(after)) url.searchParams.append("after", after + " 00:00:00");
    if (regex.test(before)) url.searchParams.append("before", before + " 00:00:00");
    if (location != "") url.searchParams.append("loaction", location);

    if (token != null && token != "") {
        headers = {
            "Authorization": "Meeting " + token
        };
    }

    let queryResult = document.getElementById("query-result");

    fetch(url, {
        method: "GET",
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            queryResult.innerHTML = "";
            json.data.forEach(element => {
                let tr = document.createElement("tr");
                tr.id = "meeting-" + element.id;

                let name = document.createElement("td");
                let a = document.createElement("a");
                a.href = "/meeting.html?id=" + element.id; 
                a.innerHTML = element.name;
                name.appendChild(a);
                tr.appendChild(name);

                meetingFields.forEach(key => {
                    let td = document.createElement("td");
                    td.innerHTML = element[key];
                    tr.appendChild(td);
                });
                queryResult.appendChild(tr);
            })
        }
    })
}
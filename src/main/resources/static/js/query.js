getMenus();

window.onload = function() {
    this.loadMenus();
    let afterDateBox = this.createDateBox();
    let beforeDateBox = this.createDateBox();
    let separated = this.document.createElement("span")

    afterDateBox.id = "after-date-box";
    beforeDateBox.id = "before-date-box";
    separated.textContent = " - ";

    let dateSelectBox = this.document.getElementById("date-select-box");
    dateSelectBox.appendChild(afterDateBox);
    dateSelectBox.appendChild(separated);
    dateSelectBox.appendChild(beforeDateBox);
};

function query() {
    let url = new URL("/api/meeting/get", "http://" + document.domain);

    let id = document.getElementById("id").value;
    let name = document.getElementById("name").value;
    let after = stringifyDate(document.getElementById("after-date-box"));
    let before = stringifyDate(document.getElementById("before-date-box"));
    let location = document.getElementById("location").value;

    let token = window.localStorage.token;
    let headers;

    if (id != "") url.searchParams.append("id", id);
    if (name != "") url.searchParams.append("name", name);
    url.searchParams.append("after", after);
    url.searchParams.append("before", before);
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

                let time = document.createElement("td");
                time.innerHTML = parseDate(element.time);
                tr.appendChild(time);

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
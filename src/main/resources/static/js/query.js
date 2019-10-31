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
            for (let index = 0; index < json.data.length; index++) {
                const element = json.data[index];
                let tr = document.createElement("tr");
                let name = document.createElement("td");
                let time = document.createElement("time");
                let location = document.createElement("td");
                let star = document.createElement("td");
                let hotel = document.createElement("td");
                let comment = document.createElement("td");

                tr.id = "meeting-" + element.id;
                name.innerHTML = element.name;
                time.innerHTML = element.time;
                location.innerHTML = element.location;
                star.innerHTML = element.star;
                hotel.innerHTML = element.hotel;
                comment.innerHTML = element.comment;

                tr.appendChild(name);
                tr.appendChild(time);
                tr.appendChild(location);
                tr.appendChild(star);
                tr.appendChild(hotel);
                tr.appendChild(comment);

                queryResult.appendChild(tr);
            }
        }
    })
}
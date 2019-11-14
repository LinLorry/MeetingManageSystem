getProfile();
getMenus();

window.onload = function() {
    loadMenus();

    let getStartSoonUrl = "/api/meeting/getStartSoon";
    let getNewestUrl = "/api/meeting/getNewest";
    let getHotUrl = "/api/meeting/getNewest";

    let startSoonMeeitngsTbody = 
        this.document.getElementById("start-soon-meeitngs-tbody");
    let newestMeetingsTbody = 
        this.document.getElementById("newest-meetings-tbody");
    let hotMeetingsTbody = 
        this.document.getElementById("hot-meetings-tbody");

    this.extractMeetings(getStartSoonUrl, startSoonMeeitngsTbody);
    this.extractMeetings(getNewestUrl, newestMeetingsTbody);
    this.extractMeetings(getHotUrl, hotMeetingsTbody);
}

function extractMeetings(url, tbody) {
    fetch(url, { method: "GET" , headers: { "Authorization": "Meeting " + localStorage.token}})
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            json.data.forEach(elem => {
                let tr = document.createElement("tr");
                
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
                tbody.appendChild(tr);
            })
        }
    })
}
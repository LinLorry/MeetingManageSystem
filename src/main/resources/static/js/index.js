getMenus();
const meetingFields = ["name", "time", "location", "start", "hotel", "comment"];

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
    fetch(url, { method: "GET" })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            json.data.forEach(elem => {
                let tr = document.createElement("tr");
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
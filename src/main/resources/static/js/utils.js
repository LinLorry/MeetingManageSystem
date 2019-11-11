const meetingFields = ["time", "location", "start", "hotel", "comment"];

function judgeLogin() {
    let token = localStorage.token;
    let userProfileURL = "/api/user/profile";

    if (token != ""  && token != null) {
        fetch(userProfileURL, {
            headers: {
                "Authorization": "Meeting " + token
            },
            method: "GET"
        })
        .then(function(response) {
            if(response.ok) {
                window.location.href = "/index.html";
            }
        })
    } else {
        window.location.href = "/index.html";
    }
}
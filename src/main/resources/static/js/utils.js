const meetingFields = ["location", "star", "hotel", "comment"];

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

function getDateString(time) {
    let date = new Date(time);

    return date.getFullYear() + '年' 
    + date.getMonth() + '月' 
    + date.getDay() + '日' 
    + date.getHours() + '点' 
    + date.getMinutes() + '分';
}
const meetingFields = ["location", "star", "hotel", "comment"];

function getProfile() {
    let url = "/api/user/profile";
    let headers = {
        "Authorization": "Meeting " + localStorage.token
    };

    fetch(url, {
        method: "GET",
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            window.localStorage.profile = JSON.stringify(json.data);
        }
    })
}

function getMenus() {
    let url = "/api/user/menus";

    fetch(url, {
        headers: {
            "Authorization": "Meeting " + window.localStorage.token
        },
        method: "GET"
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            window.localStorage.menus = JSON.stringify(json.data);
            loadMenus();
        }
    });
}

function loadMenus() {
    let select_ul = document.getElementById("select-ul");
    let menus = this.JSON.parse(window.localStorage.menus);

    if (menus.length != 0) {
        select_ul.innerHTML = "";
        menus.forEach(element => {
            let li = this.document.createElement("li");
            let a = this.document.createElement("a");
            li.appendChild(a);
    
            a.innerHTML = element.name;
            a.href = element.url;
    
            select_ul.appendChild(li);
        });

        let li = this.document.createElement("li");
        let a = this.document.createElement("a");
        li.appendChild(a);

        a.innerHTML = "登出";
        a.href = "javascript:logout()";
        select_ul.appendChild(li);
    }
}

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

function getUserId() {
    let profile = this.JSON.parse(window.localStorage.profile);
    return profile.id;
}

function getDateString(time) {
    let date = new Date(time);

    return date.getFullYear() + '年' 
    + date.getMonth() + '月' 
    + date.getDay() + '日' 
    + date.getHours() + '点' 
    + date.getMinutes() + '分';
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("menus");
    localStorage.removeItem("profile");
    location.href="/index.html";
}
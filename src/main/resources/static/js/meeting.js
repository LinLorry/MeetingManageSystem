getMenus();

const needFieldMap = {
    needGender: "性别",
    needIdCard: "身份证号",
    needName: "姓名",
    needOrganization: "工作单位"
}

var id = getId();

window.onload = function() {
    loadMenus();

    if (id === this.undefined) return;

    let url = new URL("/api/meeting/get", "http://" + document.domain);

    url.searchParams.append("id", id);
    let token = this.localStorage.token;
    let headers;

    if (token != null && token != "") {
        headers = {
            "Authorization": "Meeting " + token
        };
    }

    this.fetch(url, {
        method: "GET",
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            let data = json.data[0];

            let name = document.getElementById("name");
            let hostUser = document.getElementById("host-user");
            let time = document.getElementById("time");
            let location = document.getElementById("location");
            let star = document.getElementById("star");
            let comment = document.getElementById("comment");
            let needList = document.getElementById("need-list");

            name.innerHTML = data.name;
            hostUser.innerHTML = data.holdUser.name;
            time.innerHTML = getDateString(data.time)
            location.innerHTML = data.location;
            star.innerHTML = data.star;
            comment.innerHTML = data.comment;

            for (const key in needFieldMap) {
                if (data[key]) {
                    let li = document.createElement("li");
                    li.innerHTML = needFieldMap[key];
                    needList.appendChild(li);
                }
            }

            if (needList.children.length == 0) {
                document.getElementById("need-list-li").style.display = "none";
            }

            if (data.needParticipateTime) {
                document.getElementById("ParticipateTime-label").style.display = "block";
            }

        } else {
            let commonBox = document.getElementById("comment-box");
            let message = document.createElement("p");

            message.innerHTML = json.message;
            message.style.fontSize = "1.4em";
            commonBox.innerHTML = ""
            commonBox.appendChild(message);
        }
    })
}

function join() {
    let url = "/api/meeting/join";
    let participateTime = joinBox.participateTime.value;
    let needHotel = joinBox.needHotel.checked;
    let data = {
        meetingId: id,
        participateTime: participateTime,
        needHotel: needHotel
    };
    let token = this.localStorage.token;

    if (token == null || token == "" || token === undefined) {
        window.alert("报名会议需先登陆！")
        location.href = "/login.html"
        return;
    }

    let headers = {
        'Authorization': "Meeting " + token,
        'content-type': 'application/json'
    };

    fetch(url, {
        body: JSON.stringify(data),
        method: "POST",
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        var hint = document.getElementById("hint");
        hint.style.display = "block"; 
        hint.innerHTML = json.message;
    })

}

function getId() {
    let search = this.location.search;

    if (search.indexOf("?") == -1) {
        this.location.href = "/index.html";
        return undefined;
    }
    
    search = search.substr(1);
    let result = search.split("=")
    
    if (result[0] != "id") {
        this.location.href = "/index.html";
        return undefined;
    }

    return result[1];
}

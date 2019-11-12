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

    let getMeetingUrl = new this.URL("/api/meeting/get", "http://" + document.domain);
    let getHoldUser = new this.URL("/api/meeting/getHoldUser", "http://" + document.domain);

    getMeetingUrl.searchParams.append("id", id);
    getHoldUser.searchParams.append("id", id);

    this.fetch(getMeetingUrl, { method: "GET" })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            let data = json.data[0];

            let name = document.getElementById("name");
            let time = document.getElementById("time");
            let needList = document.getElementById("need-list");

            name.innerHTML = data.name;
            time.innerHTML = getDateString(data.time)

            meetingFields.forEach(key => {
                const i = data[key];
                let field = document.getElementById(key);
                if (i === null) {
                    field.innerHTML = "无";
                } else {
                    field.innerHTML = i;
                }
            });

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

    this.fetch(getHoldUser, { method: "GET" })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            let holdUser = document.getElementById("host-user");
            holdUser.innerHTML = json.data.username;
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

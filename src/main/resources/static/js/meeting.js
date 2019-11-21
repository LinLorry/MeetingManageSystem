getMenus();

const needFieldMap = {
    needGender: '性别',
    needIdCard: '身份证号',
    needName: '姓名',
    needOrganization: '工作单位'
}

var id = getId();

var checkTimeBox = createTimeBox();
checkTimeBox.id = 'check-time-box';

window.onload = function() {
    this.loadMenus();
    document.getElementById("participateTime-label").appendChild(checkTimeBox);
    let joinDataBox = document.getElementById('join-data-box');
    joinDataBox.style.display = 'none';

    if (id === this.undefined) return;
    qrcode.src = '/api/meeting/QRCode?id=' + id;

    profileJson = this.localStorage.profile;
    let profile = null;

    if (profileJson !== null && profileJson !== this.undefined && profileJson.length !== 0) {
        profile = this.JSON.parse(this.localStorage.profile);
    }
    
    let actionBox = this.document.getElementById('action-box');

    let getMeetingUrl = new this.URL('/api/meeting/get', 'http://' + document.domain);
    let getHoldUser = new this.URL('/api/meeting/getHoldUser', 'http://' + document.domain);

    getMeetingUrl.searchParams.append('id', id);
    getHoldUser.searchParams.append('id', id);

    this.fetch(getMeetingUrl, { method: 'GET' })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            let data = json.data[0];

            let name = document.getElementById('name');
            let time = document.getElementById('time');
            let needList = document.getElementById('need-list');

            name.textContent = data.name;
            time.textContent = parseDate(data.time)

            meetingFields.forEach(key => {
                const i = data[key];
                let field = document.getElementById(key);
                if (i === null) {
                    field.textContent = '无';
                } else {
                    field.textContent = i;
                }
            });

            for (const key in needFieldMap) {
                if (data[key]) {
                    let li = document.createElement('li');
                    li.textContent = needFieldMap[key];
                    needList.appendChild(li);
                }
            }

            if (needList.children.length == 0) {
                document.getElementById('need-list-li').style.display = 'none';
            }

            if (data.needParticipateTime) {
                document.getElementById('participateTime-label').style.display = 'block';
            }
        } else {
            let commonBox = document.getElementById('comment-box');
            let message = document.createElement('p');

            message.textContent = json.message;
            message.style.fontSize = '1.4em';
            commonBox.innerHTML = ''
            commonBox.appendChild(message);
        }
    })

    this.fetch(getHoldUser, { method: 'GET' })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            let holdUser = document.getElementById('host-user');
            holdUser.textContent = json.data.username;

            if (profile !== null) {
                if (json.data.id === profile.id) {
                    let showJoinUrl = '/meetingJoin.html?id=' + id;
                    let downloadExcel = document.createElement("button");
                    let showJoin = document.createElement("button");
    
                    actionBox.appendChild(downloadExcel);
                    actionBox.appendChild(showJoin);
    
                    downloadExcel.textContent = '下载参加会议人员信息Excel';
                    downloadExcel.onclick = downloadJoinExcel;
    
                    showJoin.textContent = '查看参加会议人员信息';
                    showJoin.onclick = function() {location.href = showJoinUrl};

                    actionBox.style.display = 'block';
    
                    document.getElementById('joinBox').style.display = 'none';
                } else {
                    let haveJoinUrl = '/api/meeting/haveJoin?id=' + id;
                    let token = localStorage.token;
                    let headers = {
                        'Authorization': 'Meeting ' + token,
                    };

                    fetch(haveJoinUrl, {
                        headers: headers
                    })
                    .then(response => response.json())
                    .then(function(json) {
                        if (json.status === 1) {
                            if (!json.data) {
                                let joinButton = document.createElement("button");
                                actionBox.appendChild(joinButton);
    
                                joinButton.textContent = '参加会议'
                                joinButton.onclick = join;
                                actionBox.style.display = 'block';
                            }
                        }
                    });
                    document.getElementById("join-data-box").style.display = 'block';
                }
            }            
        }
    });
}

function downloadJoinExcel() {
    let url = '/api/meeting/getJoinExcel?id=' + id;

    let token = localStorage.token;
    let headers = {
        'Authorization': 'Meeting ' + token,
    };

    fetch(url, {
        method: "GET",
        headers: headers
    })
    .then(response => response.blob())
    .then(blob => {
        var a = document.createElement('a');
        var url = window.URL.createObjectURL(blob);
        var filename = 'meeting' + id + "-joinUserInfo.xlsx";
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
    })
}

function join() {
    let url = '/api/meeting/join';
    let needHotel = joinBox.needHotel.checked;
    let data = {
        meetingId: id,
        participateTime: stringifyTime(checkTimeBox),
        needHotel: needHotel
    };
    let token = localStorage.token;

    if (token == null || token == '' || token === undefined) {
        window.alert('报名会议需先登陆！')
        location.href = '/login.html'
        return;
    }

    let headers = {
        'Authorization': 'Meeting ' + token,
        'content-type': 'application/json'
    };

    fetch(url, {
        body: JSON.stringify(data),
        method: 'POST',
        headers: headers
    })
    .then(response => response.json())
    .then(function(json) {
        var hint = document.getElementById('hint');
        hint.style.display = 'block'; 
        hint.textContent = json.message;
        if (json.status === 1) {
            document.getElementById('action-box').style.display = 'none';
        }
    })
}

function getId() {
    let search = this.location.search;

    if (search.indexOf('?') == -1) {
        this.location.href = '/index.html';
        return undefined;
    }
    
    search = search.substr(1);
    let result = search.split('=')
    
    if (result[0] != 'id') {
        this.location.href = '/index.html';
        return undefined;
    }

    return result[1];
}

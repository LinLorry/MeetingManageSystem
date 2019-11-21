getMenus();

window.onload = loadMenus;

const userProfileField = {
    id: 'ID：',
    username: '用户名：',
    name: '姓名：',
    gender: '性别：',
    organization: '工作单位：',
    phoneNumber: '手机号码：',
    idCard: '身份证号：'
}

function getUserInfo() {
    let username = getUserForm.username.value
    let url = '/api/admin/user?username=' + username;
    let header = {
        'Authorization': 'Meeting ' + localStorage.token
    };

    fetch(url, {
        method: 'GET',
        headers: header
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status === 1) {
            flushUserInfo(json.data);
        } else {
            disposeHint(json.message);
        }
    });
}

function deleteUser() {
    let id = deleteUserForm.id.value
    let url = '/api/admin/user?id=' + id;
    let header = {
        'Authorization': 'Meeting ' + localStorage.token
    };

    fetch(url, {
        method: 'DELETE',
        headers: header
    })
    .then(response => response.json())
    .then(function(json) {
        disposeHint(json.message);
    });
}

function deleteMeeting() {
    let id = deleteMeetingForm.id.value
    let url = '/api/admin/meeting?id=' + id;
    let header = {
        'Authorization': 'Meeting ' + localStorage.token
    };

    fetch(url, {
        method: 'DELETE',
        headers: header
    })
    .then(response => response.json())
    .then(function(json) {
        disposeHint(json.message);
    });
}

function flushUserInfo(data) {
    let commentBox = document.getElementById('comment-box');
    commentBox.innerHTML = '';

    let profile = document.createElement('ul');
    profile.className = 'message';
    commentBox.appendChild(profile);

    for (var key in data) {
        let li = document.createElement('li');
        li.textContent = userProfileField[key]
        if (key !== 'gender') {
            li.textContent += data[key];
        } else {
            if (data[key]) {
                li.textContent += '女';
            } else {
                li.textContent += '男';
            }
        }
        profile.appendChild(li);
    }
}

function registry() {
    let url = "/api/user/registry";
    let username = registryForm.username.value;
    let password = registryForm.password.value;
    let repassword = registryForm.repassword.value;
    let name = registryForm.name.value;
    let male = registryForm.gender[0].checked;
    let female = registryForm.gender[1].checked;
    let idCard = registryForm.idCard.value;
    let organization = registryForm.organization.value;
    let hint = document.getElementById("hint");

    if (username.length === 0) {
        hint.style.display = "block";
        hint.innerHTML = "用户名不能为空！"
        return;
    } else if (password.length === 0) {
        hint.style.display = "block";
        hint.innerHTML = "密码不能为空！"
        return;
    } else if (password !== repassword) {
        hint.style.display = "block";
        hint.innerHTML = "两次输入密码不一致！"
        return;
    }

    let data = {
        username: username,
        password: password        
    };
    
    if (name.length !== 0) {
        data.name = name;
    }

    if (male) {
        data.gender = false;
    } else if (female) {
        data.gender = true;
    }

    if (idCard.length !== 0) {
        data.idCard = idCard;
    }

    if (organization.length !== 0) {
        data.organization = organization;
    }

    fetch(url, {
        body: JSON.stringify(data),
        headers: {
            'content-type': 'application/json;charset=UTF-8'
        },
        method: "POST"
    })
    .then(response => response.json())
    .then(function(json) {
        console.log(json.status);
        if (json.status === 1) {
            window.location.href = "/login.html";
        } else {
            hint.style.display = "block";
            hint.innerHTML = json.message;
        }
    })
}